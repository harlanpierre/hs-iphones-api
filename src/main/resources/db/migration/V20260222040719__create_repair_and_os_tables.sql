-- 1. Atualizações na tabela Product
ALTER TABLE product ADD COLUMN internal_notes TEXT;

-- 2. Tabela de Histórico de Status do Produto
CREATE TABLE product_status_history (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    old_status VARCHAR(50),
    new_status VARCHAR(50) NOT NULL,
    reason VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_history_product FOREIGN KEY (product_id) REFERENCES product(id) ON DELETE CASCADE
);

-- 3. Tabela de Reparos Internos (BuyBack Refurbishment)
CREATE TABLE IF NOT EXISTS product_repair (
    id BIGSERIAL PRIMARY KEY,
    phone_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    part_cost NUMERIC(19, 2) NOT NULL,
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_repair_phone FOREIGN KEY (phone_id) REFERENCES product(id),
    CONSTRAINT fk_repair_part FOREIGN KEY (part_id) REFERENCES product(id)
);

-- 4. Tabela de Ordens de Serviço (Assistência Externa)
CREATE TABLE service_order (
    id BIGSERIAL PRIMARY KEY,
    client_id BIGINT NOT NULL,
    device_model VARCHAR(100) NOT NULL,
    device_imei_serial VARCHAR(50),
    reported_issue TEXT NOT NULL,
    diagnostic TEXT,
    status VARCHAR(50) NOT NULL,
    labor_cost NUMERIC(19, 2) DEFAULT 0.0,
    parts_cost NUMERIC(19, 2) DEFAULT 0.0,
    discount_amount NUMERIC(19, 2) DEFAULT 0.0,
    total_amount NUMERIC(19, 2) DEFAULT 0.0,
    internal_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_os_client FOREIGN KEY (client_id) REFERENCES client(id)
);

-- 5. Peças consumidas na Ordem de Serviço
CREATE TABLE service_order_item (
    id BIGSERIAL PRIMARY KEY,
    service_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price NUMERIC(19, 2) NOT NULL,
    subtotal NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_os_item_os FOREIGN KEY (service_order_id) REFERENCES service_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_os_item_product FOREIGN KEY (product_id) REFERENCES product(id)
);