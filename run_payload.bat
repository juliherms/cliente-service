@echo off
rem ############################################################
rem # run_payloads.bat
rem # Dispara múltiplos POST /api/clientes com payloads variados
rem # Gera arquivo temp para JSON inline, evita JsonEOFException
rem # Remove barras invertidas para evitar parsing incorreto
rem ############################################################

setlocal EnableDelayedExpansion

rem URL do serviço
set "URL=http://localhost:8080/api/clientes"

rem Header adicional
set "ORIGEM_HEADER=sistemaOrigem: SISTEMA_EMPRESTIMOS"

rem Nome do arquivo temporário
set "TEMP_JSON=%~dp0\temp_payload.json"

echo ==============================================
echo Iniciando carga de payloads para %URL%
echo Header adicional: %ORIGEM_HEADER%
echo ==============================================

rem ----------------------------------------------------------
rem # MODO 1: ler todos os arquivos .json dentro de payloads\
rem ----------------------------------------------------------
if exist payloads (
    for %%F in (payloads\*.json) do (
        echo [Arquivo] %%~nxF
        echo [Enviando JSON de arquivo:]
        type "%%F"
        echo.
        curl -X POST ^
             -H "Content-Type: application/json" ^
             -H "%ORIGEM_HEADER%" ^
             --data @%%F ^
             %URL%
        echo.
        echo ------------------------------------------
        echo.
    )
) else (
    echo Pasta "payloads\" nao encontrada. Pulando leitura de arquivos.
)

rem ----------------------------------------------------------
rem # MODO 2: definir payloads inline (chama a funcao :post)
rem ----------------------------------------------------------
echo.
echo ===== Disparando payloads inline =====
call :post "{\"cpf\":\"12345678901\",\"nome\":\"JoãoSilva\",\"dataNascimento\":\"1990-05-15\",\"rendaMensal\":5000.00,\"scoreCredito\":750,\"aposentado\":false,\"profissao\":\"Desenvolvedor\"};"
call :post "{\"cpf\":\"98765432100\",\"nome\":\"MariaOliveira\",\"dataNascimento\":\"1985-12-08\",\"rendaMensal\":8500.50,\"scoreCredito\":820,\"aposentado\":false,\"profissao\":\"Engenheira\"};"
rem -- adicione quantos call :post quiser --

echo.
echo ========== FIM ==========
echo.
pause
endlocal
exit /b

rem ##########################################################
rem # Função auxiliar: executa o POST recebendo o JSON como %1
rem # Escreve payload em arquivo temp removendo barras invertidas
rem ##########################################################
:post
    set "PAYLOAD=%~1"
    echo [Inline JSON original:]
    echo !PAYLOAD!
    echo.
    rem Remove barras invertidas
    set "CLEAN=!PAYLOAD:\=!"
    rem Grava o JSON limpo no arquivo temporário
    >"%TEMP_JSON%" echo !CLEAN!
    echo [JSON enviado sem barras:]
    echo !CLEAN!
    echo.
    rem Executa o POST usando o arquivo temporário
    curl -X POST ^
         -H "Content-Type: application/json" ^
         -H "%ORIGEM_HEADER%" ^
         --data @"%TEMP_JSON%" ^
         %URL%
    echo.
    echo ------------------------------------------
    echo.
    exit /b
