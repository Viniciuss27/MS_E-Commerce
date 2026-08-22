CREATE TABLE tb_notificacao (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    mensagem VARCHAR(255) NOT NULL,
    data_envio TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notificacao_pedido ON tb_notificacao(pedido_id);