package vinix.dto;

import vinix.entities.TipoNotificacao;

import java.time.OffsetDateTime;

public record NotificacaoResponseDTO(
    Long id,
    Long pedidoId,
    TipoNotificacao tipo,
    String mensagem,
    OffsetDateTime dataEnvio
) {}