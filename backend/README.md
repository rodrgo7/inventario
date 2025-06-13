# Backend - Sistema de Inventário

Aplicação backend Spring Boot para o Sistema de Inventário.

## Tecnologias

- Java 21
- Spring Boot 3.x
- Spring Data MongoDB
- Spring Security
- Maven

## Estrutura do Projeto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/inventario/
│   │   │       ├── config/      # Classes de configuração
│   │   │       ├── controller/  # Controladores REST
│   │   │       ├── model/       # Modelos de domínio
│   │   │       ├── repository/  # Repositórios MongoDB
│   │   │       └── service/     # Lógica de negócios
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Classes de teste
└── pom.xml
```

## Primeiros Passos

### Pré-requisitos

- Java 21 ou superior
- Maven
- MongoDB

### Executando Localmente

1. Inicie o MongoDB:
```bash
mongod
```

2. Execute a aplicação:
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### Executando com Docker

```bash
docker build -t inventario-backend .
docker run -p 8080:8080 inventario-backend
```

## Documentação da API

A documentação da API está disponível em:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Configuração

A aplicação pode ser configurada através do `application.properties` ou variáveis de ambiente:

```properties
# Servidor
server.port=8080

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/inventario

# Segurança
spring.security.user.name=admin
spring.security.user.password=admin
```

## Desenvolvimento

### Compilando

```bash
./mvnw clean package
```

### Testes

```bash
./mvnw test
```

## Contribuindo

Por favor, leia o [README.md](../README.md) principal para detalhes sobre nosso código de conduta e o processo para enviar pull requests. 