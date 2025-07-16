# Guia Docker - API de Clientes

Este guia explica como executar a API de Clientes usando Docker e Docker Compose.

## Pré-requisitos

- Docker 20.10+
- Docker Compose 2.0+

## Executando com Docker Compose

### 1. Executar toda a stack (recomendado)

```bash
# Construir e executar todos os serviços
docker-compose up --build

# Executar em background
docker-compose up -d --build
```

### 2. Executar apenas o banco de dados

```bash
# Executar apenas o MySQL
docker-compose up mysql -d

# Aguardar o banco estar pronto
docker-compose logs -f mysql
```

### 3. Verificar status dos serviços

```bash
# Ver status de todos os serviços
docker-compose ps

# Ver logs da aplicação
docker-compose logs -f cliente-api

# Ver logs do banco
docker-compose logs -f mysql
```

## Serviços Disponíveis

### API de Clientes
- **URL**: http://localhost:8080
- **Health Check**: http://localhost:8080/api/clientes/health
- **Swagger** (se configurado): http://localhost:8080/swagger-ui.html

### Banco MySQL
- **Host**: localhost
- **Porta**: 3306
- **Database**: cliente_db
- **Usuário**: cliente_user
- **Senha**: cliente_pass

### Adminer (Interface Web para MySQL)
- **URL**: http://localhost:8081
- **Sistema**: MySQL
- **Servidor**: mysql
- **Usuário**: cliente_user
- **Senha**: cliente_pass
- **Base de dados**: cliente_db

## Comandos Úteis

### Parar todos os serviços
```bash
docker-compose down
```

### Parar e remover volumes (CUIDADO: apaga dados do banco)
```bash
docker-compose down -v
```

### Reconstruir apenas a aplicação
```bash
docker-compose build cliente-api
docker-compose up -d cliente-api
```

### Executar comandos dentro do container
```bash
# Acessar shell do container da aplicação
docker-compose exec cliente-api sh

# Acessar MySQL
docker-compose exec mysql mysql -u cliente_user -p cliente_db
```

### Ver logs em tempo real
```bash
# Todos os serviços
docker-compose logs -f

# Apenas a aplicação
docker-compose logs -f cliente-api

# Apenas o banco
docker-compose logs -f mysql
```

## Construindo Imagem Docker Manualmente

### 1. Construir a imagem
```bash
docker build -t cliente-api:latest .
```

### 2. Executar apenas a aplicação (sem banco)
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_USERNAME=cliente_user \
  -e DB_PASSWORD=cliente_pass \
  -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/cliente_db" \
  cliente-api:latest
```

## Variáveis de Ambiente

### Aplicação
- `SPRING_PROFILES_ACTIVE`: Perfil do Spring (prod, dev, test)
- `DB_USERNAME`: Usuário do banco de dados
- `DB_PASSWORD`: Senha do banco de dados
- `SPRING_DATASOURCE_URL`: URL de conexão com o banco
- `JAVA_OPTS`: Opções da JVM (ex: -Xmx512m)

### MySQL
- `MYSQL_ROOT_PASSWORD`: Senha do usuário root
- `MYSQL_DATABASE`: Nome do banco de dados
- `MYSQL_USER`: Usuário da aplicação
- `MYSQL_PASSWORD`: Senha do usuário da aplicação

## Troubleshooting

### Aplicação não conecta no banco
1. Verificar se o MySQL está rodando: `docker-compose ps`
2. Verificar logs do MySQL: `docker-compose logs mysql`
3. Aguardar o health check do MySQL passar
4. Verificar variáveis de ambiente

### Porta já está em uso
```bash
# Verificar qual processo está usando a porta
sudo lsof -i :8080
sudo lsof -i :3306

# Parar serviços conflitantes ou alterar portas no docker-compose.yml
```

### Problemas de permissão
```bash
# Dar permissão para o diretório de dados do MySQL
sudo chown -R 999:999 ./mysql_data
```

### Limpar tudo e recomeçar
```bash
# Parar e remover containers, redes e volumes
docker-compose down -v --remove-orphans

# Remover imagens
docker rmi $(docker images "cliente-api*" -q)

# Reconstruir tudo
docker-compose up --build
```

## Monitoramento

### Health Checks
- **Aplicação**: `curl http://localhost:8080/api/clientes/health`
- **MySQL**: `docker-compose exec mysql mysqladmin ping`

### Métricas
- **Logs da aplicação**: `docker-compose logs cliente-api`
- **Uso de recursos**: `docker stats`

## Backup e Restore

### Backup do banco
```bash
docker-compose exec mysql mysqldump -u cliente_user -p cliente_db > backup.sql
```

### Restore do banco
```bash
docker-compose exec -T mysql mysql -u cliente_user -p cliente_db < backup.sql
```

