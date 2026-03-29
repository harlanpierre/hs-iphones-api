-- 1. Tabela de Planos
CREATE TABLE plan (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    price_monthly NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    max_products INTEGER NOT NULL DEFAULT 0,
    max_clients INTEGER NOT NULL DEFAULT 0,
    max_users INTEGER NOT NULL DEFAULT 0,
    max_sales_per_month INTEGER NOT NULL DEFAULT 0,
    max_service_orders_per_month INTEGER NOT NULL DEFAULT 0,
    features_json TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabela de Assinaturas
CREATE TABLE subscription (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL UNIQUE,
    plan_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    canceled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_subscription_tenant FOREIGN KEY (tenant_id) REFERENCES tenant(id),
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES plan(id)
);

CREATE INDEX idx_subscription_tenant_id ON subscription(tenant_id);
CREATE INDEX idx_subscription_status ON subscription(status);

-- 3. Seed: Planos padrão
INSERT INTO plan (id, name, slug, description, price_monthly, max_products, max_clients, max_users, max_sales_per_month, max_service_orders_per_month) VALUES
(1, 'Gratuito', 'free', 'Plano gratuito para teste. Ideal para conhecer o sistema.', 0.00, 20, 30, 1, 15, 10),
(2, 'Básico', 'basic', 'Para lojas pequenas. Funcionalidades essenciais com limites ampliados.', 79.90, 100, 200, 3, 100, 50),
(3, 'Profissional', 'pro', 'Para lojas em crescimento. Limites elevados e suporte prioritário.', 149.90, 500, 1000, 10, 500, 200),
(4, 'Empresarial', 'enterprise', 'Sem limites. Para operações de grande porte.', 299.90, 999999, 999999, 999999, 999999, 999999);

-- 4. Vincular tenant existente ao plano Empresarial (dados pré-existentes não devem ser limitados)
INSERT INTO subscription (tenant_id, plan_id, status) VALUES (1, 4, 'ACTIVE');

-- 5. Resetar sequences
SELECT setval('plan_id_seq', (SELECT MAX(id) FROM plan));
