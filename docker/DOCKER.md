# Docker e CI/CD

Este documento descreve como usar Docker e CI/CD para o projeto.

## Estrutura

- `Dockerfile.backend`: Build do backend Spring Boot
- `Dockerfile.frontend`: Build do frontend Angular
- `nginx.conf`: Configuração do Nginx para frontend
- `docker/docker-compose.yml`: Orquestração local
- `docker/docker-compose.prod.yml`: Orquestração para produção
- `.dockerignore`: Arquivos ignorados no build Docker
- `.github/workflows/ci-cd.yml`: Pipeline CI/CD

## Comandos Docker

### Build local

```bash
# Build de ambos os serviços
docker compose -f docker/docker-compose.yml build

# Build apenas backend
docker build -f docker/Dockerfile.backend -t bip-backend:latest .

# Build apenas frontend
docker build -f docker/Dockerfile.frontend -t bip-frontend:latest .
```

### Executar localmente

```bash
# Iniciar todos os serviços
docker compose -f docker/docker-compose.yml up -d

# Ver logs
docker compose -f docker/docker-compose.yml logs -f

# Parar serviços
docker compose -f docker/docker-compose.yml down

# Parar e remover volumes
docker compose -f docker/docker-compose.yml down -v
```

### Configurar URL da API no frontend

O frontend injeta a URL da API a partir da variável de ambiente `FRONTEND_API_URL` (padrão `/api/v1`). Para sobrescrever:

```bash
export FRONTEND_API_URL=http://localhost:8080/api/v1
docker compose -f docker/docker-compose.yml build frontend

Para apontar o proxy do Nginx para um backend específico, utilize:

```bash
export BACKEND_API_HOST=api.seu-dominio.com
export BACKEND_API_PORT=8443
docker compose -f docker/docker-compose.yml up -d
```
```

Para produção:

```bash
export FRONTEND_API_URL=https://api.seu-dominio.com/api/v1
export BACKEND_API_HOST=api.seu-dominio.com
export BACKEND_API_PORT=8443
docker compose -f docker/docker-compose.prod.yml up -d
```

## Acessos

Após iniciar os containers:

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console

## CI/CD

O pipeline GitHub Actions está configurado em `.github/workflows/ci-cd.yml`.

### Workflows

1. **Build Backend**: Compila EJB e Backend modules
2. **Build Frontend**: Compila aplicação Angular
3. **Docker Build**: Cria e publica imagens Docker no GitHub Container Registry
4. **Test**: Executa testes unitários

### Execução

O pipeline executa automaticamente em:
- Push para `main` ou `develop`
- Pull Requests para `main` ou `develop`

### Imagens Docker

As imagens são publicadas no GitHub Container Registry:
- `ghcr.io/<repo>/<repo>-backend:latest`
- `ghcr.io/<repo>/<repo>-frontend:latest`

## Produção

Para usar em produção:

```bash
# Configurar variáveis de ambiente
export REGISTRY=ghcr.io
export IMAGE_NAME=seu-repo

# Usar docker-compose.prod.yml
docker compose -f docker/docker-compose.prod.yml up -d
```

## Troubleshooting

### Backend não inicia

1. Verificar logs: `docker compose -f docker/docker-compose.yml logs backend`
2. Verificar se a porta 8080 está livre
3. Verificar health check: `curl http://localhost:8080/api-docs`

### Frontend não conecta ao backend

1. Verificar se o backend está rodando
2. Verificar configuração do Nginx (`docker/nginx.conf`)
3. Verificar variáveis de ambiente no frontend

### Build falha

1. Verificar se todas as dependências estão corretas
2. Verificar estrutura do projeto Maven
3. Verificar logs do build: `docker compose -f docker/docker-compose.yml build --no-cache`


