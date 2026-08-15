CREATE TABLE tb_categoria (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(150) NOT NULL
);

CREATE INDEX idx_categoria_nome ON tb_categoria(nome);