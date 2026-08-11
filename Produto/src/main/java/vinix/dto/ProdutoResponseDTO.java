package vinix.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        String descricao,
        String sku,
        BigDecimal preco,
        Integer estoque,
        Long categoriaId,
        Boolean ativo,
        OffsetDateTime dataCriacao
) {}
