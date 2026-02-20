-- 1. Remove a coluna IMEI antiga da tabela product
ALTER TABLE product DROP COLUMN imei;

-- 2. Cria uma tabela para armazenar os IMEIs (1 Produto -> N IMEIs)
CREATE TABLE product_imei (
    product_id BIGINT NOT NULL,
    imei VARCHAR(20) NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 3. Índice para buscar IMEI rapidamente
CREATE INDEX idx_product_imei_search ON product_imei(imei);