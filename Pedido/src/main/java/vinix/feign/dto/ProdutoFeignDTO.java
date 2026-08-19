package vinix.feign.dto;

import java.math.BigDecimal;

public record ProdutoFeignDTO(
    Long id,
    String nome,
    BigDecimal preco
) {}