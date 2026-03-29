-- Adiciona campo email na tabela users (nullable para compatibilidade com registros existentes)
ALTER TABLE users ADD COLUMN email VARCHAR(255);
