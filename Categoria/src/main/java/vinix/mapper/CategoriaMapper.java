package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vinix.dtos.CategoriaRequestDTO;
import vinix.dtos.CategoriaResponseDTO;
import vinix.entities.Categoria;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

  CategoriaResponseDTO toDto(Categoria entity);

  @Mapping(target = "id", ignore = true)
  Categoria toEntity(CategoriaRequestDTO dto);
}
