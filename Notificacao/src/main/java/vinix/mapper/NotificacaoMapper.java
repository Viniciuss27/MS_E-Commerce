package vinix.mapper;

import org.mapstruct.Mapper;
import vinix.dto.NotificacaoResponseDTO;
import vinix.entities.Notificacao;

@Mapper(componentModel = "spring")
public interface NotificacaoMapper {

    NotificacaoResponseDTO toDTO(Notificacao entity);
}