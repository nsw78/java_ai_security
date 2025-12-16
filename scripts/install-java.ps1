# Script para instalar Java 21 no Windows
# Execute como Administrador

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Instalador Java 21" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Verificar se Chocolatey está instalado
if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
    Write-Host "Chocolatey não encontrado. Instalando..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Execute este comando como Administrador:" -ForegroundColor Yellow
    Write-Host 'Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString("https://community.chocolatey.org/install.ps1"))' -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Ou instale manualmente:" -ForegroundColor Yellow
    Write-Host "1. Acesse: https://adoptium.net/temurin/releases/?version=21" -ForegroundColor Cyan
    Write-Host "2. Baixe o JDK 21 para Windows x64" -ForegroundColor Cyan
    Write-Host "3. Execute o instalador" -ForegroundColor Cyan
    Write-Host "4. Marque 'Add to PATH' durante a instalação" -ForegroundColor Cyan
    exit 1
}

Write-Host "Instalando Java 21 (Eclipse Temurin) via Chocolatey..." -ForegroundColor Yellow
Write-Host ""

# Instalar Java 21
choco install openjdk21 -y

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✓ Java 21 instalado com sucesso!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Verificando instalação..." -ForegroundColor Yellow
    
    # Atualizar PATH na sessão atual
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
    
    Start-Sleep -Seconds 2
    
    try {
        $javaVersion = java -version 2>&1 | Select-String "version"
        Write-Host "✓ $javaVersion" -ForegroundColor Green
    } catch {
        Write-Host "⚠ Java instalado, mas pode precisar reiniciar o terminal" -ForegroundColor Yellow
    }
    
    Write-Host ""
    Write-Host "Próximo passo: Execute o setup.ps1 para configurar o projeto" -ForegroundColor Cyan
} else {
    Write-Host ""
    Write-Host "✗ Erro ao instalar Java 21" -ForegroundColor Red
    Write-Host "  Tente instalar manualmente de: https://adoptium.net" -ForegroundColor Yellow
    exit 1
}

