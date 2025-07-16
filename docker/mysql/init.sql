-- Script de inicialização do banco MySQL para a API de Clientes

-- Criar banco de dados se não existir
CREATE DATABASE IF NOT EXISTS cliente_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Usar o banco de dados
USE cliente_db;

-- Criar usuário se não existir
CREATE USER IF NOT EXISTS 'cliente_user'@'%' IDENTIFIED BY 'cliente_pass';

-- Conceder privilégios
GRANT ALL PRIVILEGES ON cliente_db.* TO 'cliente_user'@'%';

-- Aplicar mudanças
FLUSH PRIVILEGES;

-- Configurações de performance
SET GLOBAL innodb_buffer_pool_size = 128M;
SET GLOBAL max_connections = 200;

-- Log de inicialização
SELECT 'Banco de dados cliente_db inicializado com sucesso!' AS status;

