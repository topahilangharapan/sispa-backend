@echo off
setlocal

:: Deteksi OS (Windows Command Prompt tidak memiliki uname, jadi asumsikan Windows)
echo Checking OS...
set OS_TYPE=Windows

:: Jalankan Docker Compose
echo Starting Docker Compose...
docker-compose up -d --build
if %ERRORLEVEL% neq 0 (
    echo Failed to start Docker Compose.
    exit /b 1
)

:: Tunggu PostgreSQL siap
echo Waiting for PostgreSQL to be ready...
:wait_postgres
docker exec db-radiant-sispa pg_isready -U admin >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo PostgreSQL is not ready yet. Retrying in 2 seconds...
    timeout /t 2 /nobreak >nul
    goto wait_postgres
)

:: Inisialisasi Database
echo Initializing Databases...
docker exec db-radiant-sispa createdb -U admin sispa_dev
docker exec db-radiant-sispa createdb -U admin sispa_prod

echo Databases created successfully.
exit /b 0
