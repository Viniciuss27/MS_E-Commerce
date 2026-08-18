package vinix.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequestDTO(
    @NotNull(message = "O ID do cliente é obrigatório")
    Long clienteId,

    @NotEmpty(message = "O pedido precisa ter ao menos um item")
    @Valid
    List<ItemPedidoRequestDTO> itens
) {}
