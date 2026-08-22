package vinix.events;

public record EstoqueFalhouEvent(
    Long pedidoId,
    String motivo
) {}
