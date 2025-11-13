@echo off
REM Limpa a tela e define cores (se suportado pelo terminal, senão apenas texto)
cls
echo ==========================================
echo      INICIANDO AUTOMACAO BANTADS
echo ==========================================

echo.
echo [1/3] Parando e removendo containers antigos...
docker-compose down --remove-orphans

echo.
echo [2/3] Construindo imagens e subindo containers...
REM O flag --build garante que as alterações no código sejam refletidas
docker-compose up --build -d

echo.
echo [3/3] Verificando status...
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCESSO] Containers iniciados!
    echo ------------------------------------------
    echo Frontend: http://localhost:4200
    echo Gateway:  http://localhost:3000
    echo ------------------------------------------
    echo Lista de containers rodando:
    docker-compose ps
) else (
    echo.
    echo [ERRO] Houve um problema ao subir o Docker.
)
pause