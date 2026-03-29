-- Índices para queries frequentes e filtros

-- Client: busca por nome (search) e soft delete
CREATE INDEX idx_client_name ON client(name);
CREATE INDEX idx_client_deleted ON client(deleted) WHERE deleted = false;

-- Supplier: busca por cpf_cnpj e soft delete
CREATE INDEX idx_supplier_cpf_cnpj ON supplier(cpf_cnpj);
CREATE INDEX idx_supplier_deleted ON supplier(deleted) WHERE deleted = false;

-- Product: filtros por status, categoria e soft delete
CREATE INDEX idx_product_status ON product(status);
CREATE INDEX idx_product_category_status ON product(category, status);
CREATE INDEX idx_product_deleted ON product(deleted) WHERE deleted = false;

-- Sale: filtros por status, client_id, created_at
CREATE INDEX idx_sale_status ON sale(status);
CREATE INDEX idx_sale_client_id ON sale(client_id);
CREATE INDEX idx_sale_created_at ON sale(created_at);

-- Service Order: filtros por status, client_id
CREATE INDEX idx_service_order_status ON service_order(status);
CREATE INDEX idx_service_order_client_id ON service_order(client_id);

-- Sale Item: FK usado em joins frequentes
CREATE INDEX idx_sale_item_sale_id ON sale_item(sale_id);
CREATE INDEX idx_sale_item_product_id ON sale_item(product_id);

-- Payment: FK usado em joins
CREATE INDEX idx_payment_sale_id ON payment(sale_id);

-- Service Order Item: FK usado em joins
CREATE INDEX idx_service_order_item_os_id ON service_order_item(service_order_id);

-- Product Status History: busca por product_id
CREATE INDEX idx_product_status_history_product_id ON product_status_history(product_id);

-- Users: busca por username (login)
CREATE INDEX idx_users_username ON users(username);
