-- 1. Cria a tabela de Endereço primeiro (pois Client depende dela)
CREATE TABLE address (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255),
    number VARCHAR(20),
    complement VARCHAR(255),
    district VARCHAR(100),
    city VARCHAR(100),
    state VARCHAR(50),
    zip_code VARCHAR(20)
);

-- 2. Cria a tabela Client com chave estrangeira para Address
CREATE TABLE client (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_client_address FOREIGN KEY (address_id) REFERENCES address(id)
);