-- Tabela de Pedidos
CREATE TABLE tb_pedido (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    total NUMERIC(19, 2) NOT NULL,
    data_criacao TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de Itens do Pedido
CREATE TABLE tb_item_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id)
);

CREATE INDEX idx_pedido_cliente ON tb_pedido(cliente_id);
CREATE INDEX idx_item_pedido_pedido ON tb_item_pedido(pedido_id);