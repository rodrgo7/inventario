#!/bin/bash

set -e

echo "🛑 Parando e removendo containers, volumes e orfaos..."
docker-compose down --volumes --remove-orphans

echo "🧹 Removendo imagens orfas..."
docker image prune -af

echo "🚀 Subindo aplicacao com novo build..."
docker-compose up --build
