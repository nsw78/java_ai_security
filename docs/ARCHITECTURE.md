# 🏗️ Architecture Overview

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Client Layer                          │
│  (Web App, Mobile App, API Gateway, etc.)                    │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │ HTTPS / REST API
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    AI Security API                            │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Security Filter Chain                     │  │
│  │  • JWT Authentication                                   │  │
│  │  • CORS Handling                                       │  │
│  │  • Rate Limiting (Bucket4j)                            │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              Request Processing Pipeline                │  │
│  │                                                         │  │
│  │  1. Authentication & Authorization                      │  │
│  │  2. Rate Limit Check                                   │  │
│  │  3. Policy Engine Evaluation                           │  │
│  │  4. Prompt Sanitization                                │  │
│  │  5. Injection Detection                                │  │
│  │  6. Risk Score Calculation                             │  │
│  │  7. Decision: Block or Proceed                         │  │
│  │  8. LLM API Call (if allowed)                          │  │
│  │  9. Audit Logging (async)                              │  │
│  └───────────────────────────────────────────────────────┘  │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        │
        ┌───────────────┼───────────────┐
        │               │               │
        ▼               ▼               ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  PostgreSQL  │ │    Redis     │ │  LLM Provider│
│  (Audit Logs)│ │ (Rate Limit) │ │ (OpenAI, etc)│
│  (Users)     │ │   (Cache)    │ │              │
└──────────────┘ └──────────────┘ └──────────────┘
```

## Component Architecture

### 1. Security Layer

#### Authentication & Authorization
- **JWT-based authentication**
- **Role-based access control (RBAC)**
- **User plans (FREE, PREMIUM, ENTERPRISE)**

#### Rate Limiting
- **Token bucket algorithm** (Bucket4j)
- **Redis-backed distributed rate limiting**
- **Plan-based limits**

### 2. Security Services

#### Prompt Sanitization Service
- Removes control characters
- Filters HTML/XML tags
- Detects SQL injection patterns
- Normalizes whitespace

#### Injection Detection Service
- Pattern-based detection
- Configurable regex patterns
- Extensible for ML-based detection

#### Risk Score Calculator
Multi-factor risk assessment:
- Injection patterns detected: +40 points
- Very long prompts (>10k): +20 points
- Suspicious keywords: +30 points
- Encoding attempts: +15 points
- Repeated patterns: +25 points
- Context factors: +10-15 points

Risk Levels:
- **LOW**: 0-29
- **MEDIUM**: 30-59
- **HIGH**: 60-79
- **CRITICAL**: 80-100

#### Policy Engine
Three default policies:

1. **Restrictive**
   - No external APIs
   - Max prompt: 4000 chars
   - No domain whitelist

2. **Moderate**
   - External APIs allowed
   - Max prompt: 8000 chars
   - Whitelist: openai.com, anthropic.com

3. **Permissive**
   - External APIs allowed
   - Max prompt: 16000 chars
   - All domains allowed

### 3. Data Layer

#### PostgreSQL
- **Users table**: Authentication and authorization
- **Audit logs table**: Compliance and monitoring
- **Indexes**: Optimized for queries

#### Redis
- **Rate limiting buckets**: Per-user token buckets
- **Cache**: Frequently accessed data
- **Session storage**: (if needed)

### 4. Audit & Compliance

#### Audit Service
- **Async logging**: Non-blocking
- **Complete request/response tracking**
- **Metadata storage**: JSONB for flexibility
- **Retention policies**: Configurable

#### Compliance Features
- **LGPD ready**: Data privacy compliance
- **SOC2 ready**: Security controls
- **ISO 27001 ready**: Information security

## Request Flow

```
1. Client Request
   ↓
2. JWT Authentication Filter
   ↓
3. Security Filter Chain
   ↓
4. Rate Limit Check
   ├─ Exceeded → 429 Too Many Requests
   └─ OK → Continue
   ↓
5. Policy Engine Evaluation
   ├─ Violation → 403 Forbidden
   └─ OK → Continue
   ↓
6. Prompt Sanitization
   ↓
7. Injection Detection
   ↓
8. Risk Score Calculation
   ├─ Critical/High → Block
   └─ Low/Medium → Continue
   ↓
9. LLM API Call (if not blocked)
   ↓
10. Response to Client
   ↓
11. Async Audit Logging
```

## Technology Stack

### Core
- **Java 21**: Latest LTS with modern features
- **Spring Boot 3.2**: Enterprise framework
- **Spring Security**: Authentication & authorization
- **Spring Data JPA**: Database access

### Security
- **JWT (JJWT)**: Token-based authentication
- **Bucket4j**: Advanced rate limiting
- **BCrypt**: Password hashing

### Data
- **PostgreSQL 16**: Primary database
- **Redis 7**: Caching & rate limiting
- **Hibernate**: ORM

### API
- **OpenAPI 3 / Swagger**: API documentation
- **Spring Web**: REST API
- **Validation**: Bean validation

### DevOps
- **Docker**: Containerization
- **Docker Compose**: Local development
- **Maven**: Build tool

## Scalability Considerations

### Horizontal Scaling
- **Stateless design**: JWT tokens
- **Redis distributed**: Shared rate limiting
- **Database connection pooling**: HikariCP

### Performance
- **Async audit logging**: Non-blocking
- **Caching**: Redis for frequently accessed data
- **Database indexes**: Optimized queries
- **Connection pooling**: Efficient resource usage

### Monitoring
- **Spring Actuator**: Health checks & metrics
- **Prometheus**: Metrics export
- **Audit logs**: Complete request tracking

## Security Best Practices

1. **Defense in Depth**: Multiple security layers
2. **Least Privilege**: Role-based access
3. **Input Validation**: Sanitization & validation
4. **Rate Limiting**: Prevent abuse
5. **Audit Logging**: Complete traceability
6. **Secure Secrets**: Environment variables
7. **HTTPS**: Encrypted communication
8. **Regular Updates**: Dependency management

## Future Enhancements

- [ ] ML-based anomaly detection
- [ ] Real-time threat intelligence
- [ ] Advanced analytics dashboard
- [ ] Webhook notifications
- [ ] Multi-tenant support
- [ ] GraphQL API
- [ ] gRPC support
- [ ] Kubernetes deployment configs

