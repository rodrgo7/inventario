# Frontend - Sistema de Inventário

Aplicação frontend React para o Sistema de Inventário.

## Tecnologias

- React 18
- Vite
- React Router DOM
- Axios
- SweetAlert2
- Node.js 18+

## Estrutura do Projeto

```
frontend/
├── src/
│   ├── components/    # Componentes React
│   ├── pages/        # Componentes de página
│   ├── services/     # Serviços de API
│   ├── utils/        # Funções utilitárias
│   ├── App.jsx       # Componente principal App
│   └── main.jsx      # Ponto de entrada
├── public/           # Arquivos estáticos
├── index.html        # Template HTML
└── vite.config.js    # Configuração do Vite
```

## Primeiros Passos

### Pré-requisitos

- Node.js 18 ou superior
- npm ou yarn

### Instalação

```bash
npm install
```

### Desenvolvimento

```bash
npm run dev
```

A aplicação estará disponível em `http://localhost:5173`

### Compilando para Produção

```bash
npm run build
```

A saída da compilação estará no diretório `dist`.

### Visualizando Build de Produção

```bash
npm run preview
```

## Suporte Docker

A aplicação inclui suporte Docker para implantação em produção:

```bash
# Construir a imagem
docker build -t inventario-frontend .

# Executar o container
docker run -p 3000:80 inventario-frontend
```

## Configuração

A aplicação pode ser configurada através de variáveis de ambiente:

```env
VITE_API_URL=http://localhost:8080
```

## Diretrizes de Desenvolvimento

### Estilo de Código

- Use componentes funcionais com hooks
- Siga as melhores práticas do React
- Use tipos TypeScript apropriados quando aplicável

### Estrutura do Projeto

- Mantenha os componentes pequenos e focados
- Use convenções apropriadas de nomenclatura de arquivos
- Organize as importações adequadamente

## Contribuindo

Por favor, leia o [README.md](../README.md) principal para detalhes sobre nosso código de conduta e o processo para enviar pull requests. 