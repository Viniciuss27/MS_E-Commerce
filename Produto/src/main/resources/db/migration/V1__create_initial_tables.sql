-- Tabela de Produtos
CREATE TABLE tb_produto (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(80) NOT NULL,
    descricao VARCHAR(150) NOT NULL,
    sku VARCHAR(100) NOT NULL UNIQUE,
    preco NUMERIC(19, 2) NOT NULL,
    estoque INT NOT NULL,
    categoria_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Idempotência (Controle de Eventos do Kafka)
CREATE TABLE tb_evento_processado (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    data_processamento TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índices para otimizar buscas frequentes
CREATE INDEX idx_produto_categoria ON tb_produto(categoria_id);
CREATE INDEX idx_produto_sku ON tb_produto(sku);