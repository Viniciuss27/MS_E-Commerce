package vinix.events;

import java.util.List;

public record PedidoCriadoEvent(
    Long pedidoId,
    Long clientId,
    List<ItemPedidoEvent> itens
) {}