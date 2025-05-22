# Sistema de Gerenciamento de Estoque

Um robusto sistema de gerenciamento de estoque construído com Spring Boot, apresentando autenticação segura, integração com MongoDB e arquitetura orientada a eventos.

## 🚀 Funcionalidades

- **Autenticação e Autorização**
  - Autenticação baseada em JWT
  - Manipulação segura de senhas
  - Controle de acesso baseado em funções

- **Gerenciamento de Estoque**
  - Rastreamento de produtos
  - Controle de estoque
  - Atualizações em tempo real do inventário

- **Arquitetura Orientada a Eventos**
  - Integração com Kafka para processamento de eventos
  - Operações assíncronas
  - Notificações em tempo real

## 🛠️ Stack Tecnológica

- **Backend**
  - Java 21
  - Spring Boot 3.4.5
  - Spring Security
  - Spring Data MongoDB
  - Spring Kafka
  - Autenticação JWT

- **Banco de Dados**
  - MongoDB

- **Message Broker**
  - Apache Kafka

## 📋 Pré-requisitos

- Java 21 ou superior
- MongoDB 6.0 ou superior
- Apache Kafka (opcional, para processamento de eventos)
- Maven 3.6 ou superior

## 🔧 Configuração

A aplicação utiliza as seguintes configurações padrão:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/estoquedb
      username: admin
      password: # Defina sua senha aqui

app:
  jwt:
    secret: # Seu segredo JWT
    expiration-ms: 3600000 # 1 hora
```

## 🚀 Como Começar

1. **Clone o repositório**
   ```bash
   git clone [url-do-seu-repositorio]
   cd inventario
   ```

2. **Configure o MongoDB**
   - Instale o MongoDB se ainda não estiver instalado
   - Crie um banco de dados chamado `estoquedb`
   - Atualize as credenciais do MongoDB no arquivo `application.yaml`

3. **Compile o projeto**
   ```bash
   mvn clean install
   ```

4. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```

## 🔐 Segurança

A aplicação implementa autenticação baseada em JWT com as seguintes características:
- Armazenamento seguro de senhas
- Autenticação baseada em tokens
- Controle de acesso baseado em funções
- Expiração de token configurável

## 📦 Estrutura do Projeto

```
src/main/java/oliveiradev/inventario/
├── application/    # Serviços da aplicação e casos de uso
├── domain/        # Modelos de domínio e lógica de negócios
├── infra/         # Componentes de infraestrutura
├── interfaces/    # Controladores da API e DTOs
└── util/          # Classes utilitárias
```

## 🧪 Testes

O projeto inclui cobertura abrangente de testes:
- Testes unitários
- Testes de integração
- Testes com containers para MongoDB

Execute os testes usando:
```bash
mvn test
```

## 📝 Documentação da API

A documentação da API está disponível em:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Especificação OpenAPI: `http://localhost:8080/v3/api-docs`

## 🤝 Como Contribuir

1. Faça um fork do repositório
2. Crie sua branch de feature (`git checkout -b feature/NovaFuncionalidade`)
3. Faça commit das suas alterações (`git commit -m 'Adiciona nova funcionalidade'`)
4. Faça push para a branch (`git push origin feature/NovaFuncionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo LICENSE para detalhes.

## 👥 Autores

- Seu Nome - Trabalho inicial

## 🙏 Agradecimentos

- Equipe Spring Boot
- Equipe MongoDB
- Equipe Apache Kafka 