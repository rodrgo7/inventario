@echo off

:: rebuild.bat - Para uso no Windows

echo Parando e removendo containers, volumes e órfãos...
docker-compose down --volumes --remove-orphans

echo Limpando imagens órfãs...
docker image prune -af

echo Subindo aplicação com novo build...
docker-compose up --build

pause
