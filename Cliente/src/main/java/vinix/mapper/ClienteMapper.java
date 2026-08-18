package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vinix.dto.ClienteRequestDTO;
import vinix.dto.ClienteResponseDTO;
import vinix.dto.ClienteUpdateDTO;
import vinix.entity.Cliente;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

  ClienteResponseDTO toDTO(Cliente entity);

  @Mapping(target = "id", ignore = true)
  Cliente toEntity(ClienteRequestDTO dto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cpf", ignore = true)  // -> manter o cpf durante o update
  ClienteUpdateDTO updateEntityFromDTO(ClienteUpdateDTO dto, @MappingTarget Cliente entity);
}
