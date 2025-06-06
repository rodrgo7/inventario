@echo off

:: rebuild.bat - Para uso no Windows

echo Parando e removendo containers, volumes e orfaos...
docker-compose down --volumes --remove-orphans

echo Limpando imagens orfas...
docker image prune -af

echo Subindo aplicacao com novo build...
docker-compose up --build

pause
