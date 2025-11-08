# Execução do Projeto com Docker

Este guia descreve como subir a aplicação completa (backend + frontend) usando Docker e Docker Compose, além de listar os principais endpoints disponíveis.

## Pré-requisitos

- Docker (23.x ou superior)
- Docker Compose CLI (`docker compose`, integrado ao Docker Desktop ou instalado separadamente)

> Todos os comandos abaixo devem ser executados na raiz do repositório:


## 1. Construindo e iniciando os contêineres

```bash
# Subir os serviços em modo interativo (logs no terminal)
docker compose -f docker/docker-compose.yml up --build
```

Caso prefira rodar em segundo plano (modo detached):

```bash
docker compose -f docker/docker-compose.yml up --build -d
```

## 2. Verificando se os serviços estão rodando

```bash
# Lista os contêineres em execução
docker compose -f docker/docker-compose.yml ps

# Visualiza logs (substitua pelo serviço desejado: backend ou frontend)
docker compose -f docker/docker-compose.yml logs -f backend
```

O Docker Compose aguarda o backend ficar saudável (healthcheck em `http://backend:8080/api-docs`) antes de liberar o frontend.

## 3. Endereços de acesso

| Serviço   | URL local                                    | Observação                                 |
|-----------|----------------------------------------------|--------------------------------------------|
| Frontend  | `http://localhost:4200`                      | Aplicação Angular servida via Nginx        |
| Backend   | `http://localhost:8080`                      | API Spring Boot                             |
| Swagger UI| `http://localhost:8080/swagger-ui/index.html`| Documentação interativa                     |
| OpenAPI   | `http://localhost:8080/api-docs`             | Especificação em formato JSON               |

## 4. Endpoints principais (backend)

Base path: `http://localhost:8080/api/v1/beneficios`

| Método | Endpoint                 | Descrição                                   |
|--------|--------------------------|---------------------------------------------|
| GET    | `/`                      | Lista paginada de benefícios (`page`, `size`) |
| GET    | `/search?nome=...`       | Busca benefícios ativos pelo nome           |
| GET    | `/{id}`                  | Detalha um benefício                        |
| POST   | `/`                      | Cria um novo benefício                      |
| PUT    | `/{id}`                  | Atualiza um benefício existente             |
| DELETE | `/{id}`                  | Remove um benefício                         |
| POST   | `/transfer`              | Transfere valores entre benefícios          |

## 5. Finalizando e limpando

```bash
# Derruba os contêineres mantendo as imagens
docker compose -f docker/docker-compose.yml down

# Derruba e remove também os volumes (optional)
docker compose -f docker/docker-compose.yml down -v
```

Para reconstruir tudo do zero (imagens e cache):

```bash
docker compose -f docker/docker-compose.yml build --no-cache
```
