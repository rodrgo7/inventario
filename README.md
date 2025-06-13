# Sistema de Inventário

Sistema completo de gerenciamento de inventário desenvolvido com Spring Boot 3, React, MongoDB e Java 21.

## 🚀 Tecnologias

### Backend
- Java 21
- Spring Boot 3.1.8
- Spring Security
- Spring Data MongoDB
- JWT Authentication
- Maven
- MongoDB
- OpenAPI/Swagger

### Frontend
- React 18
- Vite
- React Router DOM
- Axios
- SweetAlert2
- Node.js 18+

## 📋 Pré-requisitos

- Java 21 ou superior
- Node.js 18 ou superior
- MongoDB
- Maven
- Docker e Docker Compose (opcional)

## 🔧 Configuração do Ambiente

### Backend

1. Clone o repositório:
```bash
git clone [url-do-repositorio]
cd inventario/backend
```

2. Configure o MongoDB:
- Instale o MongoDB localmente ou use Docker
- Crie um banco de dados chamado `estoquedb`

3. Configure as variáveis de ambiente:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/estoquedb

app:
  jwt:
    secret: [sua-chave-secreta]
    expiration-ms: 3600000
```

4. Execute o projeto:
```bash
./mvnw spring-boot:run
```

### Frontend

1. Navegue até a pasta do frontend:
```bash
cd ../frontend
```

2. Instale as dependências:
```bash
npm install
```

3. Execute o projeto:
```bash
npm run dev
```

## 🐳 Executando com Docker

1. Na raiz do projeto:
```bash
docker-compose up -d
```

## 📚 Documentação da API

A documentação da API está disponível através do Swagger UI:
- URL: `http://localhost:8080/swagger-ui.html`

## 🔐 Segurança

O sistema utiliza autenticação JWT (JSON Web Token) para proteger as rotas da API. Para desenvolvimento e testes, a autenticação pode ser desabilitada através da configuração de segurança.

## 🧪 Testes

### Backend
```bash
./mvnw test
```

### Frontend
```bash
npm test
```

## 📦 Estrutura do Projeto

```
inventario/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── oliveiradev/inventario/
│   │   │   │       ├── application/    # Serviços e DTOs
│   │   │   │       ├── domain/         # Modelos e Repositórios
│   │   │   │       ├── infra/          # Configurações e Segurança
│   │   │   │       └── interfaces/     # Controllers
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── services/
│   │   └── utils/
│   └── package.json
└── docker-compose.yml
```

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.

## 👥 Autores

- Seu Nome - Desenvolvimento inicial