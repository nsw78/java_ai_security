# 📋 Guia de Sistema de Logging Estruturado

## 🎯 Objetivo

Sistema de logging com **baixa verbosidade** mas **insights valiosos** para diagnóstico rápido de problemas.

## 🏗️ Arquitetura

### 1. **Logback com JSON Estruturado**
- Formato JSON para produção (facilita análise)
- Formato legível para desenvolvimento
- Separação de logs de erro em arquivo dedicado

### 2. **StructuredLogger - Serviço Centralizado**
- Logs estruturados com contexto (correlation ID, endpoint, user, etc.)
- Mensagens concisas mas informativas
- Integração com MDC (Mapped Diagnostic Context)

### 3. **ErrorCode - Códigos Centralizados**
- Códigos únicos para cada tipo de erro
- Facilita busca e análise
- Categorização por domínio (AUTH, VAL, PROMPT, etc.)

### 4. **GlobalExceptionHandler Melhorado**
- Captura todas as exceções
- Logging estruturado automático
- Contexto rico (endpoint, IP, correlation ID)

## 📊 Estrutura de Logs

### Logs de Erro (JSON)
```json
{
  "timestamp": "2025-12-16T12:00:00.000Z",
  "level": "ERROR",
  "logger": "com.trustai.exception.GlobalExceptionHandler",
  "message": "[AUTH_ERROR:AUTH_001] Authentication failed - invalid credentials | Root cause: BadCredentialsException",
  "service": "ai-security-api",
  "thread": "http-nio-8080-exec-1",
  "class": "com.trustai.exception.GlobalExceptionHandler",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "endpoint": "/api/v1/auth/authenticate",
  "method": "POST",
  "ip": "192.168.1.1",
  "errorType": "AUTH_ERROR",
  "errorCode": "AUTH_001",
  "stacktrace": "..."
}
```

### Logs de Info/Warn
```json
{
  "timestamp": "2025-12-16T12:00:00.000Z",
  "level": "INFO",
  "logger": "com.trustai.service.PromptSanitizationService",
  "message": "[PROMPT_ANALYSIS] Prompt analyzed - Risk: MEDIUM (45/100)",
  "service": "ai-security-api",
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "endpoint": "/api/v1/secure-prompt",
  "riskLevel": "MEDIUM",
  "riskScore": "45"
}
```

## 🔍 Códigos de Erro

### Autenticação (AUTH_*)
- `AUTH_001`: Credenciais inválidas
- `AUTH_002`: Token expirado
- `AUTH_003`: Token inválido
- `AUTH_004`: Acesso não autorizado

### Validação (VAL_*)
- `VAL_001`: Parâmetros inválidos
- `VAL_002`: Campo obrigatório ausente
- `VAL_003`: Formato de dados inválido

### Prompt Security (PROMPT_*)
- `PROMPT_001`: Injeção de prompt detectada
- `PROMPT_002`: Prompt muito longo
- `PROMPT_003`: Prompt bloqueado por política
- `PROMPT_004`: Falha na sanitização

### Rate Limiting (RATE_*)
- `RATE_001`: Limite de taxa excedido
- `RATE_002`: Erro de configuração

### Database (DB_*)
- `DB_001`: Erro de conexão
- `DB_002`: Erro na query
- `DB_003`: Violação de constraint

### Redis (REDIS_*)
- `REDIS_001`: Erro de conexão
- `REDIS_002`: Erro na operação

## 📁 Arquivos de Log

### Desenvolvimento
- `logs/ai-security-api.log` - Todos os logs
- `logs/ai-security-api-errors.log` - Apenas erros

### Produção
- `logs/ai-security-api-YYYY-MM-DD.N.log.gz` - Logs rotacionados
- `logs/ai-security-api-errors-YYYY-MM-DD.N.log.gz` - Erros rotacionados

### Configuração
- Rotação diária ou quando atinge 100MB
- Retenção: 30 dias (geral) / 90 dias (erros)
- Tamanho máximo: 3GB (geral) / 1GB (erros)

## 🎯 Uso do StructuredLogger

### Exemplo 1: Log de Erro
```java
@Autowired
private StructuredLogger structuredLogger;

try {
    // código
} catch (Exception e) {
    Map<String, String> context = new HashMap<>();
    context.put("userId", userId);
    context.put("promptId", promptId);
    
    structuredLogger.logError(
        "PROMPT_ERROR",
        ErrorCode.PROMPT_SANITIZATION_FAILED.getCode(),
        "Failed to sanitize prompt",
        e,
        context
    );
}
```

### Exemplo 2: Log de Warning
```java
Map<String, String> context = new HashMap<>();
context.put("riskScore", String.valueOf(riskScore));
context.put("threshold", "80");

structuredLogger.logWarning(
    "HIGH_RISK",
    String.format("High risk prompt detected: %d/100", riskScore),
    context
);
```

### Exemplo 3: Log de Info
```java
Map<String, String> context = new HashMap<>();
context.put("endpoint", "/api/v1/secure-prompt");
context.put("duration", "150ms");

structuredLogger.logInfo(
    "REQUEST_COMPLETED",
    "Request processed successfully",
    context
);
```

## 🔧 Configuração por Ambiente

### Desenvolvimento
- Console: Formato legível
- Arquivo: JSON estruturado
- Nível: INFO

### Produção
- Console: JSON estruturado
- Arquivo: JSON estruturado
- Nível: INFO (erros sempre logados)

## 📈 Insights Incluídos

### Sempre Incluídos
- ✅ Correlation ID (rastreamento de requisição)
- ✅ Endpoint e método HTTP
- ✅ IP do cliente
- ✅ Tipo de erro e código
- ✅ Classe da exceção
- ✅ Mensagem concisa

### Contexto Adicional (quando relevante)
- ✅ User ID
- ✅ Risk Score/Level
- ✅ Prompt ID
- ✅ Timestamp preciso
- ✅ Stack trace (apenas para erros)

## 🚫 O que NÃO é Logado

- ❌ Senhas ou tokens completos
- ❌ Dados sensíveis do usuário
- ❌ Stack traces completos em produção (apenas resumo)
- ❌ Logs de debug de bibliotecas externas
- ❌ Logs verbosos do Spring/Hibernate

## 🔍 Busca e Análise

### Buscar por Correlation ID
```bash
grep "550e8400-e29b-41d4-a716-446655440000" logs/ai-security-api.log
```

### Buscar por Error Code
```bash
grep "AUTH_001" logs/ai-security-api-errors.log
```

### Buscar por Endpoint
```bash
grep "\"endpoint\":\"/api/v1/secure-prompt\"" logs/ai-security-api.log
```

### Análise com jq (JSON)
```bash
cat logs/ai-security-api-errors.log | jq 'select(.errorCode == "AUTH_001")'
```

## 📊 Métricas de Logging

- **Verbosidade**: Baixa (apenas INFO+)
- **Insights**: Alto (contexto rico)
- **Performance**: Otimizado (async appenders)
- **Retenção**: 30-90 dias
- **Formato**: JSON estruturado

---

**Sistema projetado para facilitar diagnóstico rápido sem poluir logs com informações desnecessárias.**

