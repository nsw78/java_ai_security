# Script para iniciar a aplicação
# Uso: .\start.ps1

Write-Host "Iniciando AI Security API..." -ForegroundColor Cyan
Write-Host ""

# Verificar se Docker está rodando
Write-Host "Verificando serviços Docker..." -ForegroundColor Yellow
try {
    docker ps | Out-Null
} catch {
    Write-Host "✗ Docker não está rodando. Por favor, inicie o Docker Desktop." -ForegroundColor Red
    exit 1
}

# Verificar se os containers estão rodando
$postgresRunning = docker ps --filter "name=ai-security-postgres" --format "{{.Names}}"
$redisRunning = docker ps --filter "name=ai-security-redis" --format "{{.Names}}"

if (-not $postgresRunning) {
    Write-Host "Iniciando PostgreSQL..." -ForegroundColor Yellow
    docker-compose up -d postgres
    Start-Sleep -Seconds 3
}

if (-not $redisRunning) {
    Write-Host "Iniciando Redis..." -ForegroundColor Yellow
    docker-compose up -d redis
    Start-Sleep -Seconds 3
}

Write-Host "✓ Serviços prontos" -ForegroundColor Green
Write-Host ""

# Iniciar aplicação
Write-Host "Iniciando aplicação Spring Boot..." -ForegroundColor Cyan
Write-Host ""

if (Test-Path "mvnw.cmd") {
    & .\mvnw.cmd spring-boot:run
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn spring-boot:run
} else {
    Write-Host "✗ Maven não encontrado. Por favor, execute o setup.ps1 primeiro." -ForegroundColor Red
    exit 1
}

