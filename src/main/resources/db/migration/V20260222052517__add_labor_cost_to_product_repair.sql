-- Torna a peça e o custo da peça opcionais (para o caso de ser apenas serviço de placa)
ALTER TABLE product_repair ALTER COLUMN part_id DROP NOT NULL;
ALTER TABLE product_repair ALTER COLUMN part_cost DROP NOT NULL;

-- Adiciona os custos e descrições de mão de obra terceirizada
ALTER TABLE product_repair ADD COLUMN labor_cost NUMERIC(19, 2) DEFAULT 0.0;
ALTER TABLE product_repair ADD COLUMN labor_description VARCHAR(255);