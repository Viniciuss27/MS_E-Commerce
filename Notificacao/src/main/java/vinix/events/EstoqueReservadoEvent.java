package vinix.events;

public record EstoqueReservadoEvent(
    Long pedidoId,
    Boolean sucesso,
    String mensagem
) {}
