package vinix.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres")
        String nome,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(max = 150, message = "A descrição não pode passar de 150 caracteres")
        String descricao,

        @NotBlank(message = "O SKU é obrigatório")
        String sku,

        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço precisa ser maior que zero")
        @Digits(integer = 17, fraction = 2, message = "O preço deve ter no máximo 17 dígitos inteiros e 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "O estoque é obrigatório")
        @PositiveOrZero(message = "O estoque não pode ser negativo")
        Integer estoque,

        @NotNull(message = "A categoria é obrigatória")
        Long categoriaId
) {}