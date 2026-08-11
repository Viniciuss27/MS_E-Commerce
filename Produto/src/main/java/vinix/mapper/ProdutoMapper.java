package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vinix.dto.ProdutoRequestDTO;
import vinix.dto.ProdutoResponseDTO;
import vinix.entities.Produto;

@Mapper(componentModel = "spring")
public interface ProdutoMapper {

    ProdutoResponseDTO toDTO(Produto entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    Produto toEntity(ProdutoRequestDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ativo", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    void updateEntityFromDTO(ProdutoRequestDTO dto, @MappingTarget Produto entity);
    //atualiza o Produto já carregado do banco sem sobrescrever o id nem a dataCriacao
}