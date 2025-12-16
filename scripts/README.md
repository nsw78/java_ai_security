# 🔧 Scripts de Automação

Este diretório contém scripts PowerShell para facilitar a configuração e execução do projeto.

## 📜 Scripts Disponíveis

### 🚀 `setup.ps1`
Script completo de configuração inicial do ambiente:
- Verifica e instala dependências (Java, Maven, Docker)
- Configura variáveis de ambiente
- Compila o projeto
- Inicia os serviços Docker

**Uso:**
```powershell
.\scripts\setup.ps1
```

### ☕ `install-java.ps1`
Instala o Java 21 usando Chocolatey.

**Uso:**
```powershell
.\scripts\install-java.ps1
```

### ▶️ `start.ps1`
Inicia a aplicação e serviços Docker.

**Uso:**
```powershell
.\scripts\start.ps1
```

## ⚠️ Requisitos

- Windows PowerShell 5.1+ ou PowerShell Core 7+
- Permissões de administrador (para instalação de dependências)

## 🔒 Segurança

Antes de executar scripts pela primeira vez, você pode precisar ajustar a política de execução:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

---

**Nota:** Todos os scripts devem ser executados a partir da raiz do projeto.

