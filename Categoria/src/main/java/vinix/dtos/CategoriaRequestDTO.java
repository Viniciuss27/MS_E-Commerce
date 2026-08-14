package vinix.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequestDTO(

    @NotBlank(message = "O nome é obrigatório")
    @Size(min = 3, max = 80, message = "O nome deve ter entre 3 e 80 caracteres")
    String nome,

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 150, message = "A descrição não pode passar de 150 caracteres")
    String descricao
) {}
