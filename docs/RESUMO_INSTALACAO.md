# 📋 Resumo Rápido de Instalação

## ⚡ Instalação Rápida (Windows)

### 1. Instalar Java 21

**Opção mais rápida (com Chocolatey):**
```powershell
# Se não tem Chocolatey, instale primeiro:
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString("https://community.chocolatey.org/install.ps1"))

# Depois instale Java:
choco install openjdk21 -y
```

**Ou baixe manualmente:**
- Acesse: https://adoptium.net/temurin/releases/?version=21
- Baixe: JDK 21 para Windows x64
- Execute o instalador
- Marque "Add to PATH"

### 2. Verificar Instalação

Abra um **novo** PowerShell e execute:

```powershell
java -version
```

Deve mostrar: `openjdk version "21.x.x"`

### 3. Executar Setup Automático

No diretório do projeto:

```powershell
# Permitir scripts (apenas uma vez)
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

# Executar setup
.\setup.ps1
```

O script irá:
- ✅ Verificar Java
- ✅ Baixar dependências Maven
- ✅ Iniciar PostgreSQL e Redis no Docker
- ✅ Preparar tudo para uso

### 4. Iniciar Aplicação

```powershell
.\start.ps1
```

Ou manualmente:

```powershell
.\mvnw.cmd spring-boot:run
```

### 5. Acessar

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

---

## 🎯 Comandos Úteis

### Iniciar serviços Docker
```powershell
docker-compose up -d postgres redis
```

### Parar serviços Docker
```powershell
docker-compose down
```

### Ver logs
```powershell
docker-compose logs -f
```

### Compilar projeto
```powershell
.\mvnw.cmd clean install
```

### Executar testes
```powershell
.\mvnw.cmd test
```

---

## ❓ Problemas Comuns

### "java não é reconhecido"
- Reinicie o PowerShell/Terminal
- Verifique JAVA_HOME: `$env:JAVA_HOME`
- Reinstale Java e marque "Add to PATH"

### "Porta 8080 em uso"
- Altere em `application.yml`: `server.port: 8081`
- Ou pare o processo: `netstat -ano | findstr :8080`

### "Docker não está rodando"
- Inicie o Docker Desktop
- Aguarde alguns segundos
- Verifique: `docker ps`

---

## 📚 Documentação Completa

- [INSTALL.md](./INSTALL.md) - Guia completo de instalação
- [QUICKSTART.md](./QUICKSTART.md) - Primeiros passos com a API
- [README.md](README.md) - Documentação principal
- [ARCHITECTURE.md](./ARCHITECTURE.md) - Arquitetura do sistema

---

## ✅ Checklist Final

- [ ] Java 21 instalado e no PATH
- [ ] Docker Desktop rodando
- [ ] Dependências Maven baixadas
- [ ] PostgreSQL e Redis rodando (via Docker)
- [ ] Aplicação iniciada com sucesso
- [ ] Swagger UI acessível

---

**Precisa de ajuda?** Consulte o [INSTALL.md](./INSTALL.md) para instruções detalhadas.

