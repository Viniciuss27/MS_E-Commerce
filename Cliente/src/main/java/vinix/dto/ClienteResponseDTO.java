package vinix.dto;

import javax.swing.*;

public record ClienteResponseDTO(
    Long id,
    String nome,
    String cpf,
    String email,
    String telefone
) {}
