#!/bin/bash

set -e

echo "🛑 Parando e removendo containers, volumes e órfãos..."
docker-compose down --volumes --remove-orphans

echo "🧹 Removendo imagens órfãs..."
docker image prune -af

echo "🚀 Subindo aplicação com novo build..."
docker-compose up --build
