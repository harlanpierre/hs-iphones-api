-- 1. Tabela de Tenants (cada loja/empresa)
CREATE TABLE tenant (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tenant padrão para dados existentes
INSERT INTO tenant (id, name, slug) VALUES (1, 'HS iPhones PE', 'hs-iphones-pe');

-- 3. Adicionar tenant_id nas tabelas de dados
ALTER TABLE users ADD COLUMN tenant_id BIGINT;
ALTER TABLE client ADD COLUMN tenant_id BIGINT;
ALTER TABLE supplier ADD COLUMN tenant_id BIGINT;
ALTER TABLE product ADD COLUMN tenant_id BIGINT;
ALTER TABLE sale ADD COLUMN tenant_id BIGINT;
ALTER TABLE service_order ADD COLUMN tenant_id BIGINT;

-- 4. Vincular dados existentes ao tenant padrão
UPDATE users SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE client SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE supplier SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE product SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE sale SET tenant_id = 1 WHERE tenant_id IS NULL;
UPDATE service_order SET tenant_id = 1 WHERE tenant_id IS NULL;

-- 5. Tornar NOT NULL e adicionar FKs
ALTER TABLE users ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE client ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE supplier ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE product ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE sale ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE service_order ALTER COLUMN tenant_id SET NOT NULL;

ALTER TABLE users ADD CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE client ADD CONSTRAINT fk_client_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE supplier ADD CONSTRAINT fk_supplier_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE product ADD CONSTRAINT fk_product_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE sale ADD CONSTRAINT fk_sale_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);
ALTER TABLE service_order ADD CONSTRAINT fk_service_order_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id);

-- 6. Índices para tenant_id (performance de queries filtradas)
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_client_tenant_id ON client(tenant_id);
CREATE INDEX idx_supplier_tenant_id ON supplier(tenant_id);
CREATE INDEX idx_product_tenant_id ON product(tenant_id);
CREATE INDEX idx_sale_tenant_id ON sale(tenant_id);
CREATE INDEX idx_service_order_tenant_id ON service_order(tenant_id);

-- 7. Username agora é único POR TENANT (não globalmente)
ALTER TABLE users DROP CONSTRAINT users_username_key;
ALTER TABLE users ADD CONSTRAINT uq_users_tenant_username UNIQUE (tenant_id, username);

-- 8. Resetar a sequence do tenant para evitar conflito
SELECT setval('tenant_id_seq', (SELECT MAX(id) FROM tenant));
