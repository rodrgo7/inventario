# Sistema de Inventário

Este é um sistema de gerenciamento de inventário desenvolvido com uma arquitetura moderna e escalável.

## 🏗️ Arquitetura

O projeto é composto por dois componentes principais:

### Backend
- Desenvolvido em Java com Spring Boot
- Expõe uma API REST na porta 8080
- Configurado para ambiente de produção
- Gerenciado com Maven

### Frontend
- Aplicação web moderna
- Servido através do Nginx
- Acessível na porta 3000
- Construído com Vite

## 🚀 Como Executar

### Pré-requisitos
- Docker
- Docker Compose

### Passos para Execução

1. Clone o repositório
```bash
git clone [URL_DO_REPOSITÓRIO]
cd inventario
```

2. Execute com Docker Compose
```bash
docker-compose up -d
```

Após a execução, os serviços estarão disponíveis em:
- Frontend: http://localhost:3000
- Backend: http://localhost:8080

## 🔧 Configuração

### Backend
- Porta: 8080
- Perfil: prod
- Configurações adicionais podem ser ajustadas no arquivo `application.properties`

### Frontend
- Porta: 3000
- Configurado para se comunicar com o backend
- Nginx configurado para servir a aplicação

## 🛠️ Tecnologias Utilizadas

- **Backend**
  - Java
  - Spring Boot
  - Maven

- **Frontend**
  - Vite
  - Nginx

- **Infraestrutura**
  - Docker
  - Docker Compose

## 📝 Notas de Desenvolvimento

Para desenvolvimento local, você pode executar os serviços separadamente:

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## 🤝 Contribuindo

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request