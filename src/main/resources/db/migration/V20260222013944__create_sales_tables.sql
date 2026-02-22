-- Criação da tabela de Vendas (Sale)
CREATE TABLE sale (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    seller_name VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    total_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.0,
    discount_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.0,
    net_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT fk_sale_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- Criação da tabela de Itens da Venda (SaleItem)
CREATE TABLE sale_item (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    imei VARCHAR(20),
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(19, 2) NOT NULL,
    subtotal NUMERIC(19, 2) NOT NULL,
    warranty_end_date DATE,

    CONSTRAINT fk_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product(id)
);

-- Criação da tabela de Pagamentos (Payment)
CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    method VARCHAR(50) NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    installments INTEGER DEFAULT 1,
    buyback_product_id BIGINT,

    CONSTRAINT fk_payment_sale FOREIGN KEY (sale_id) REFERENCES sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_payment_buyback FOREIGN KEY (buyback_product_id) REFERENCES product(id)
);