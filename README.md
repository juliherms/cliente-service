# API de Cadastro e Consulta de Clientes

Esta é uma API REST desenvolvida com Spring Boot 3.x para cadastro e consulta de clientes, utilizando Flyway para migrations de banco de dados.

## Características

- **Framework**: Spring Boot 3.2.0
- **Java**: 17+
- **Banco de Dados**: H2 (em memória para desenvolvimento)
- **Migrations**: Flyway
- **Validações**: Bean Validation (JSR-303)
- **Documentação**: Swagger/OpenAPI (opcional)

## Estrutura do Cliente

```json
{
  "id": 1,
  "cpf": "12345678901",
  "nome": "João Silva",
  "dataNascimento": "1990-05-15",
  "rendaMensal": 5000.00,
  "scoreCredito": 750,
  "aposentado": false,
  "profissao": "Desenvolvedor"
}
```

## Endpoints da API

### 1. Cadastrar Cliente
- **POST** `/api/clientes`
- **Descrição**: Cadastra um novo cliente
- **Headers**: Nenhum header obrigatório
- **Body**:
```json
{
  "cpf": "12345678901",
  "nome": "João Silva",
  "dataNascimento": "1990-05-15",
  "rendaMensal": 5000.00,
  "scoreCredito": 750,
  "aposentado": false,
  "profissao": "Desenvolvedor"
}
```
- **Resposta**: 201 Created com dados do cliente criado

### 2. Buscar Cliente por CPF
- **GET** `/api/clientes/cpf/{cpf}`
- **Descrição**: Busca cliente pelo CPF
- **Headers Obrigatórios**: 
  - `sistemaOrigem`: Identificação do sistema que está fazendo a consulta
- **Exemplo**: `GET /api/clientes/cpf/12345678901`
- **Resposta**: 200 OK com dados do cliente

### 3. Buscar Cliente por ID
- **GET** `/api/clientes/{id}`
- **Descrição**: Busca cliente pelo ID
- **Headers Obrigatórios**: 
  - `sistemaOrigem`: Identificação do sistema que está fazendo a consulta
- **Exemplo**: `GET /api/clientes/1`
- **Resposta**: 200 OK com dados do cliente

### 4. Listar Clientes (Paginado)
- **GET** `/api/clientes`
- **Descrição**: Lista todos os clientes com paginação
- **Headers Obrigatórios**: 
  - `sistemaOrigem`: Identificação do sistema que está fazendo a consulta
- **Parâmetros de Query**:
  - `page`: Número da página (padrão: 0)
  - `size`: Tamanho da página (padrão: 20)
  - `sort`: Ordenação (ex: `nome,asc`)
- **Exemplo**: `GET /api/clientes?page=0&size=10&sort=nome,asc`
- **Resposta**: 200 OK com lista paginada de clientes

### 5. Buscar Clientes por Nome
- **GET** `/api/clientes/buscar?nome={nome}`
- **Descrição**: Busca clientes por nome (busca parcial, case insensitive)
- **Headers Obrigatórios**: 
  - `sistemaOrigem`: Identificação do sistema que está fazendo a consulta
- **Exemplo**: `GET /api/clientes/buscar?nome=João`
- **Resposta**: 200 OK com lista de clientes

### 6. Atualizar Cliente
- **PUT** `/api/clientes/{id}`
- **Descrição**: Atualiza dados do cliente
- **Headers**: Nenhum header obrigatório
- **Body**: Mesmo formato do cadastro
- **Resposta**: 200 OK com dados atualizados

### 7. Remover Cliente
- **DELETE** `/api/clientes/{id}`
- **Descrição**: Remove cliente pelo ID
- **Headers**: Nenhum header obrigatório
- **Resposta**: 204 No Content

### 8. Health Check
- **GET** `/api/clientes/health`
- **Descrição**: Verifica se a API está funcionando
- **Headers**: Nenhum header obrigatório
- **Resposta**: 200 OK com mensagem de status

## Validações

### Campos Obrigatórios
- `cpf`: CPF válido (11 dígitos)
- `nome`: Entre 2 e 100 caracteres
- `dataNascimento`: Data no passado
- `rendaMensal`: Valor positivo ou zero
- `scoreCredito`: Entre 0 e 1000
- `aposentado`: true ou false
- `profissao`: Entre 2 e 50 caracteres

### Regras de Negócio
- CPF deve ser único no sistema
- Data de nascimento deve ser no passado
- Renda mensal deve ser positiva ou zero
- Score de crédito deve estar entre 0 e 1000

## Header Obrigatório para Consultas

**IMPORTANTE**: Todas as operações de consulta (GET) requerem o header `sistemaOrigem` para identificar qual sistema externo está fazendo a consulta.

### Exemplo de uso:
```bash
curl -H "sistemaOrigem: SISTEMA_VENDAS" \
     -X GET http://localhost:8080/api/clientes/cpf/12345678901
```

## Tratamento de Erros

A API retorna erros estruturados no seguinte formato:

```json
{
  "message": "Descrição do erro",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/clientes",
  "timestamp": "2024-01-15 10:30:00",
  "details": ["Lista de detalhes do erro"]
}
```

### Códigos de Status
- **200**: Sucesso
- **201**: Criado com sucesso
- **204**: Removido com sucesso
- **400**: Erro de validação ou header ausente
- **404**: Cliente não encontrado
- **409**: CPF duplicado
- **500**: Erro interno do servidor

## Como Executar

### Pré-requisitos
- Java 11+
- Maven 3.6+

### Passos
1. Clone o repositório
2. Execute: `mvn clean install`
3. Execute: `mvn spring-boot:run`
4. A API estará disponível em: `http://localhost:8080`

### Console H2
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Exemplos de Uso

### Cadastrar Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "cpf": "12345678901",
    "nome": "João Silva",
    "dataNascimento": "1990-05-15",
    "rendaMensal": 5000.00,
    "scoreCredito": 750,
    "aposentado": false,
    "profissao": "Desenvolvedor"
  }'
```

### Buscar Cliente por CPF
```bash
curl -H "sistemaOrigem: SISTEMA_VENDAS" \
     -X GET http://localhost:8080/api/clientes/cpf/12345678901
```

### Listar Clientes
```bash
curl -H "sistemaOrigem: SISTEMA_VENDAS" \
     -X GET "http://localhost:8080/api/clientes?page=0&size=10"
```
## Tecnologias Utilizadas

- **Spring Boot 3.2.0**: Framework principal
- **Spring Data JPA**: Persistência de dados
- **Spring Web**: API REST
- **Spring Validation**: Validações
- **Flyway**: Migrations de banco
- **H2 Database**: Banco em memória
- **Maven**: Gerenciamento de dependências
- **SLF4J**: Logging

## Próximos Passos

- [ ] Adicionar Swagger/OpenAPI para documentação interativa
- [ ] Implementar testes unitários e de integração
- [ ] Configurar perfis para diferentes ambientes
- [ ] Adicionar autenticação e autorização
- [ ] Implementar cache com Redis
- [ ] Configurar banco PostgreSQL para produção

