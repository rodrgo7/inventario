# Sistema de Inventário

Um sistema completo de gerenciamento de inventário construído com Spring Boot, React e MongoDB.

## Estrutura do Projeto

```
inventario/
├── backend/         # Aplicação Spring Boot
├── frontend/        # Aplicação React
├── scripts-mongodb/ # Scripts de inicialização do MongoDB
└── docker-compose.yml
```

## Início Rápido

1. Clone o repositório:
```bash
git clone [url-do-repositorio]
cd inventario
```

2. Inicie a aplicação usando Docker Compose:
```bash
docker-compose up -d
```

A aplicação estará disponível em:
- Frontend: http://localhost:3000
- API Backend: http://localhost:8080
- MongoDB: localhost:27017

## Desenvolvimento

### Pré-requisitos
- Java 17 ou superior
- Node.js 18 ou superior
- Docker e Docker Compose
- MongoDB (se executando localmente)

### Executando Localmente

1. Backend:
```bash
cd backend
./mvnw spring-boot:run
```

2. Frontend:
```bash
cd frontend
npm install
npm run dev
```

## Documentação

Para documentação detalhada sobre cada componente, consulte:
- [Documentação do Backend](./backend/README.md)
- [Documentação do Frontend](./frontend/README.md)

## Contribuindo

1. Faça um Fork do repositório
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.