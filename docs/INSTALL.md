# 📦 Guia de Instalação - Windows

Este guia irá ajudá-lo a instalar todas as dependências necessárias para executar o projeto AI Security API.

## ✅ Checklist de Instalação

- [ ] Java 21 (JDK)
- [ ] Maven 3.8+ (ou usar Maven Wrapper incluído)
- [x] Docker Desktop (já instalado ✓)
- [ ] IDE (opcional, mas recomendado: IntelliJ IDEA ou VS Code)

---

## 1. Instalar Java 21 (JDK)

### Opção A: Usando Chocolatey (Recomendado)

Se você tem Chocolatey instalado:

```powershell
choco install openjdk21
```

### Opção B: Download Manual

1. **Acesse**: https://adoptium.net/temurin/releases/?version=21
2. **Selecione**:
   - Version: 21 (LTS)
   - Operating System: Windows
   - Architecture: x64
   - Package Type: JDK
3. **Baixe** o instalador `.msi`
4. **Execute** o instalador
5. **Marque** "Add to PATH" durante a instalação

### Opção C: Usando SDKMAN (se tiver WSL)

```bash
sdk install java 21.0.1-tem
```

### Verificar Instalação

Abra um **novo** PowerShell e execute:

```powershell
java -version
```

Você deve ver algo como:
```
openjdk version "21.0.1" 2024-04-16
OpenJDK Runtime Environment Temurin-21.0.1+12 (build 21.0.1+12)
OpenJDK 64-Bit Server VM Temurin-21.0.1+12 (build 21.0.1+12, mixed mode)
```

### Configurar JAVA_HOME

1. Abra **Configurações do Sistema** → **Variáveis de Ambiente**
2. Clique em **Novo** em Variáveis do Sistema
3. Nome: `JAVA_HOME`
4. Valor: `C:\Program Files\Eclipse Adoptium\jdk-21.0.1+12` (ajuste conforme sua instalação)
5. Clique em **OK**

Ou via PowerShell (como Administrador):

```powershell
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Eclipse Adoptium\jdk-21.0.1+12', 'Machine')
```

---

## 2. Instalar Maven (Opcional - Projeto já inclui Maven Wrapper)

### Opção A: Usando Chocolatey

```powershell
choco install maven
```

### Opção B: Download Manual

1. **Acesse**: https://maven.apache.org/download.cgi
2. **Baixe**: `apache-maven-3.9.5-bin.zip`
3. **Extraia** para: `C:\Program Files\Apache\maven`
4. **Adicione ao PATH**: `C:\Program Files\Apache\maven\bin`

### Verificar Instalação

```powershell
mvn -version
```

**Nota**: O projeto inclui Maven Wrapper (`mvnw.cmd`), então você pode usar o projeto sem instalar Maven globalmente.

---

## 3. Verificar Docker

Docker já está instalado! ✅

Verifique se está rodando:

```powershell
docker ps
```

Se não estiver rodando, inicie o **Docker Desktop**.

---

## 4. Configurar o Projeto

### 4.1. Clonar/Baixar o Projeto

Se ainda não fez, certifique-se de estar no diretório do projeto:

```powershell
cd C:\Users\t.nwa_tfsports\Documents\Projetos_IA\javaapi
```

### 4.2. Baixar Dependências Maven

```powershell
.\mvnw.cmd clean install -DskipTests
```

Ou se tiver Maven instalado:

```powershell
mvn clean install -DskipTests
```

### 4.3. Iniciar Serviços (PostgreSQL e Redis)

```powershell
docker-compose up -d postgres redis
```

Aguarde alguns segundos e verifique:

```powershell
docker ps
```

Você deve ver os containers `ai-security-postgres` e `ai-security-redis` rodando.

---

## 5. Executar a Aplicação

### Opção A: Usando Maven Wrapper (Recomendado)

```powershell
.\mvnw.cmd spring-boot:run
```

### Opção B: Usando Maven (se instalado)

```powershell
mvn spring-boot:run
```

### Opção C: Usando IDE

1. Abra o projeto no IntelliJ IDEA ou VS Code
2. Aguarde o Maven baixar as dependências
3. Execute a classe `AiSecurityApiApplication`

---

## 6. Verificar se Está Funcionando

Após iniciar, acesse:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health
- **API Docs**: http://localhost:8080/api-docs

---

## 🐛 Troubleshooting

### Erro: "java não é reconhecido"

1. Verifique se Java está instalado: `java -version`
2. Verifique se `JAVA_HOME` está configurado
3. Reinicie o PowerShell/Terminal
4. Verifique o PATH: `$env:PATH`

### Erro: "Porta 8080 já em uso"

Altere a porta no `application.yml`:

```yaml
server:
  port: 8081
```

### Erro: "Não consegue conectar ao PostgreSQL"

1. Verifique se o container está rodando: `docker ps`
2. Verifique os logs: `docker logs ai-security-postgres`
3. Reinicie: `docker-compose restart postgres`

### Erro: "Não consegue conectar ao Redis"

1. Verifique se o container está rodando: `docker ps`
2. Verifique os logs: `docker logs ai-security-redis`
3. Reinicie: `docker-compose restart redis`

### Erro de Maven: "Could not resolve dependencies"

1. Verifique sua conexão com a internet
2. Limpe o cache: `.\mvnw.cmd clean`
3. Force atualização: `.\mvnw.cmd clean install -U`

---

## 📚 Próximos Passos

Após a instalação bem-sucedida:

1. Leia o [QUICKSTART.md](./QUICKSTART.md) para começar a usar a API
2. Explore a documentação Swagger
3. Configure variáveis de ambiente (veja `env.example`)

---

## 💡 Dicas

- Use **IntelliJ IDEA Community Edition** (grátis) para melhor experiência
- Configure o **Git** se ainda não tiver
- Considere usar **Windows Terminal** para melhor experiência no PowerShell

---

## 🆘 Precisa de Ajuda?

Se encontrar problemas:

1. Verifique os logs da aplicação: `logs/ai-security-api.log`
2. Verifique os logs do Docker: `docker-compose logs`
3. Consulte a documentação do Spring Boot: https://spring.io/projects/spring-boot

