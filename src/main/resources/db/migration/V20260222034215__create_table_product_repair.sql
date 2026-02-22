-- Adiciona uma coluna no produto para somar os custos extras (além do valor pago no BuyBack)
ALTER TABLE product ADD COLUMN repair_cost NUMERIC(19, 2) DEFAULT 0.0;

-- Cria a tabela de histórico de reparos
CREATE TABLE product_repair (
    id BIGSERIAL PRIMARY KEY,
    phone_id BIGINT NOT NULL,
    part_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    part_cost NUMERIC(19, 2) NOT NULL, -- O custo da peça no momento do uso
    notes VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_repair_phone FOREIGN KEY (phone_id) REFERENCES product(id),
    CONSTRAINT fk_repair_part FOREIGN KEY (part_id) REFERENCES product(id)
);