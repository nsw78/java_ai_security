# 🚀 Instalar Java 21 - Passo a Passo

## Status Atual
❌ Java **NÃO** está instalado

## 📥 Opção 1: Download Direto (Recomendado)

### Passo 1: Baixar Java 21
1. Abra seu navegador
2. Acesse: **https://adoptium.net/temurin/releases/?version=21**
3. Na página, selecione:
   - **Operating System**: Windows
   - **Architecture**: x64
   - **Package Type**: JDK
4. Clique em **Download** no arquivo `.msi`

### Passo 2: Instalar
1. Execute o arquivo `.msi` baixado
2. Clique em **Next** nas telas
3. **IMPORTANTE**: Marque a opção **"Add to PATH"** ou **"Set JAVA_HOME variable"**
4. Clique em **Install**
5. Aguarde a instalação terminar
6. Clique em **Finish**

### Passo 3: Verificar Instalação
1. **Feche** o PowerShell atual
2. Abra um **novo** PowerShell
3. Execute:
   ```powershell
   java -version
   ```
4. Deve aparecer algo como:
   ```
   openjdk version "21.0.1" 2024-04-16
   OpenJDK Runtime Environment Temurin-21.0.1+12
   ```

---

## 📦 Opção 2: Usando Chocolatey (Avançado)

Se preferir usar linha de comando:

### Passo 1: Instalar Chocolatey
Execute no PowerShell **como Administrador**:

```powershell
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString("https://community.chocolatey.org/install.ps1"))
```

### Passo 2: Instalar Java 21
```powershell
choco install openjdk21 -y
```

### Passo 3: Verificar
Feche e abra um novo PowerShell:
```powershell
java -version
```

---

## ✅ Após Instalar

Depois que o Java estiver instalado:

1. **Feche e abra um novo PowerShell** (importante!)
2. Execute:
   ```powershell
   cd C:\Users\t.nwa_tfsports\Documents\Projetos_IA\javaapi
   .\setup.ps1
   ```

Isso irá:
- ✅ Verificar Java
- ✅ Baixar dependências
- ✅ Iniciar PostgreSQL e Redis
- ✅ Preparar tudo para uso

---

## 🔍 Verificar se Funcionou

Execute no PowerShell:
```powershell
java -version
```

Se aparecer a versão do Java, está funcionando! ✅

Se ainda der erro:
- Reinicie o computador
- Ou adicione manualmente ao PATH (veja abaixo)

---

## 🛠️ Adicionar Java ao PATH Manualmente

Se o Java foi instalado mas não aparece no PATH:

1. Encontre onde o Java foi instalado (geralmente):
   - `C:\Program Files\Eclipse Adoptium\jdk-21.x.x.x-hotspot`
   - `C:\Program Files\Java\jdk-21`

2. Abra **Configurações do Sistema** → **Variáveis de Ambiente**

3. Em **Variáveis do Sistema**, encontre **Path** e clique em **Editar**

4. Clique em **Novo** e adicione:
   ```
   C:\Program Files\Eclipse Adoptium\jdk-21.0.1.12-hotspot\bin
   ```
   (Ajuste o caminho conforme sua instalação)

5. Clique em **OK** em todas as janelas

6. Feche e abra um novo PowerShell

7. Teste: `java -version`

---

## 📞 Precisa de Ajuda?

Se tiver problemas:
1. Verifique se baixou a versão **JDK 21** (não JRE)
2. Certifique-se de marcar "Add to PATH" durante instalação
3. Reinicie o PowerShell após instalar
4. Se necessário, reinicie o computador

---

**Link Direto para Download:**
👉 https://adoptium.net/temurin/releases/?version=21

