package vinix.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteUpdateDTO(

    @NotBlank(message = "O nome é obrigatorio")
    @Size(min = 2, max = 80, message = "deve ter entre 2 e 80 caracteres")
    String nome,

    @NotBlank(message = "O email é obrigatorio")
    @Size(min = 10, max = 120, message = "O email deve conter entre 10 e 120 caracteres")
    @Email(message = "Email, digite um Email valido")
    String email,

    @NotBlank(message = "O telefone é obrigatorio")
    @Size(min = 9, max = 20, message = "digite um numero ex: (xx) xxxxx-xxxx")
    String telefone
) {}