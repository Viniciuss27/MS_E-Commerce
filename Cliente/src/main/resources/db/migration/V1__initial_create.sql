CREATE TABLE tb_clientes (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    telefone VARCHAR(20) NOT NULL
);

CREATE INDEX idx_cliente_cpf ON tb_clientes(cpf);
CREATE INDEX idx_cliente_email ON tb_clientes(email);