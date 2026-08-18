package vinix.dto;

import java.math.BigDecimal;


public record ItemPedidoResponseDTO(
    Long id,
    Long produtoId,
    Integer quantidade,
    BigDecimal precoUnitario
) {}

