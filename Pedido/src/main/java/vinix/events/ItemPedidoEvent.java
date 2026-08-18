package vinix.events;

public record ItemPedidoEvent(
    Long produtoId,
    Integer quantidade
) {}