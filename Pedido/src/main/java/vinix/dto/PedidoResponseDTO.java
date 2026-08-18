package vinix.dto;

import vinix.entities.StatusPedido;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PedidoResponseDTO(
    Long id,
    Long clienteId,
    StatusPedido status,
    List<ItemPedidoResponseDTO> itens,
    BigDecimal total,
    OffsetDateTime dataCriacao
) {}
