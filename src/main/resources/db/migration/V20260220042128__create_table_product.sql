CREATE TABLE product (
     id BIGSERIAL PRIMARY KEY,
     name VARCHAR(255) NOT NULL,
     description TEXT,
     sku VARCHAR(50) UNIQUE,
     category VARCHAR(20) NOT NULL,
     status VARCHAR(20) NOT NULL DEFAULT 'DISPONIVEL',

    -- Preços
     purchase_price NUMERIC(19, 2) NOT NULL,
     sale_price NUMERIC(19, 2) NOT NULL,

    -- Estoque
     quantity INTEGER NOT NULL DEFAULT 0,
     min_stock INTEGER DEFAULT 0,

    -- Especificidades
     imei VARCHAR(20), -- REMOVIDO UNIQUE (Permite BuyBack/Histórico)
     compatible_model VARCHAR(100),

    -- Garantia do Fornecedor (Opcional se vier de cliente)
     supplier_warranty_start_date DATE,
     supplier_warranty_end_date DATE,

    -- Relacionamentos (Origem pode ser Fornecedor OU Cliente)
     supplier_id BIGINT,
     client_id BIGINT,

     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_product_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id),
     CONSTRAINT fk_product_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- Índice para melhorar a busca de histórico de IMEI
CREATE INDEX idx_product_imei ON product(imei);