# Script de Setup Automático para Windows
# Execute como Administrador: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  AI Security API - Setup Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar Java
Write-Host "[1/5] Verificando Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1 | Select-String "version"
    if ($javaVersion) {
        Write-Host "✓ Java encontrado: $javaVersion" -ForegroundColor Green
        $javaVersionNumber = $javaVersion -replace ".*version `"([0-9]+).*", '$1'
        if ([int]$javaVersionNumber -lt 21) {
            Write-Host "⚠ Java 21 ou superior é necessário. Versão atual: $javaVersionNumber" -ForegroundColor Red
            Write-Host "  Por favor, instale Java 21 de: https://adoptium.net/temurin/releases/?version=21" -ForegroundColor Yellow
            exit 1
        }
    }
} catch {
    Write-Host "✗ Java não encontrado!" -ForegroundColor Red
    Write-Host "  Instalando Java 21..." -ForegroundColor Yellow
    
    # Tentar instalar via Chocolatey
    if (Get-Command choco -ErrorAction SilentlyContinue) {
        Write-Host "  Usando Chocolatey para instalar..." -ForegroundColor Yellow
        choco install openjdk21 -y
    } else {
        Write-Host "  Chocolatey não encontrado." -ForegroundColor Yellow
        Write-Host "  Por favor, instale Java 21 manualmente:" -ForegroundColor Yellow
        Write-Host "  https://adoptium.net/temurin/releases/?version=21" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "  Ou instale Chocolatey primeiro:" -ForegroundColor Yellow
        Write-Host "  https://chocolatey.org/install" -ForegroundColor Cyan
        exit 1
    }
}

# Verificar Maven (opcional, pois temos Maven Wrapper)
Write-Host ""
Write-Host "[2/5] Verificando Maven..." -ForegroundColor Yellow
try {
    $mvnVersion = mvn -version 2>&1 | Select-String "Apache Maven"
    if ($mvnVersion) {
        Write-Host "✓ Maven encontrado" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ Maven não encontrado (opcional - usando Maven Wrapper)" -ForegroundColor Yellow
}

# Verificar Docker
Write-Host ""
Write-Host "[3/5] Verificando Docker..." -ForegroundColor Yellow
try {
    $dockerVersion = docker --version
    if ($dockerVersion) {
        Write-Host "✓ Docker encontrado: $dockerVersion" -ForegroundColor Green
        
        # Verificar se Docker está rodando
        try {
            docker ps | Out-Null
            Write-Host "✓ Docker está rodando" -ForegroundColor Green
        } catch {
            Write-Host "⚠ Docker instalado mas não está rodando" -ForegroundColor Yellow
            Write-Host "  Por favor, inicie o Docker Desktop" -ForegroundColor Yellow
        }
    }
} catch {
    Write-Host "✗ Docker não encontrado!" -ForegroundColor Red
    Write-Host "  Por favor, instale Docker Desktop:" -ForegroundColor Yellow
    Write-Host "  https://www.docker.com/products/docker-desktop" -ForegroundColor Cyan
    exit 1
}

# Baixar dependências Maven
Write-Host ""
Write-Host "[4/5] Baixando dependências Maven..." -ForegroundColor Yellow
try {
    if (Test-Path "mvnw.cmd") {
        Write-Host "  Usando Maven Wrapper..." -ForegroundColor Yellow
        & .\mvnw.cmd clean install -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Dependências baixadas com sucesso" -ForegroundColor Green
        } else {
            Write-Host "✗ Erro ao baixar dependências" -ForegroundColor Red
            exit 1
        }
    } elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
        Write-Host "  Usando Maven instalado..." -ForegroundColor Yellow
        mvn clean install -DskipTests
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✓ Dependências baixadas com sucesso" -ForegroundColor Green
        } else {
            Write-Host "✗ Erro ao baixar dependências" -ForegroundColor Red
            exit 1
        }
    } else {
        Write-Host "⚠ Maven Wrapper não encontrado e Maven não está instalado" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Erro ao baixar dependências: $_" -ForegroundColor Red
}

# Iniciar serviços Docker
Write-Host ""
Write-Host "[5/5] Iniciando serviços Docker (PostgreSQL e Redis)..." -ForegroundColor Yellow
try {
    if (Test-Path "docker-compose.yml") {
        docker-compose up -d postgres redis
        Start-Sleep -Seconds 5
        
        # Verificar se os containers estão rodando
        $postgresRunning = docker ps --filter "name=ai-security-postgres" --format "{{.Names}}"
        $redisRunning = docker ps --filter "name=ai-security-redis" --format "{{.Names}}"
        
        if ($postgresRunning -and $redisRunning) {
            Write-Host "✓ Serviços iniciados com sucesso" -ForegroundColor Green
            Write-Host "  - PostgreSQL: $postgresRunning" -ForegroundColor Green
            Write-Host "  - Redis: $redisRunning" -ForegroundColor Green
        } else {
            Write-Host "⚠ Alguns serviços podem não estar rodando" -ForegroundColor Yellow
            Write-Host "  Verifique com: docker ps" -ForegroundColor Yellow
        }
    } else {
        Write-Host "⚠ docker-compose.yml não encontrado" -ForegroundColor Yellow
    }
} catch {
    Write-Host "✗ Erro ao iniciar serviços: $_" -ForegroundColor Red
}

# Resumo
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Setup Concluído!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Yellow
Write-Host "  1. Execute a aplicação: .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host "  2. Acesse: http://localhost:8080/swagger-ui.html" -ForegroundColor White
Write-Host "  3. Leia o docs/QUICKSTART.md para começar" -ForegroundColor White
Write-Host ""

