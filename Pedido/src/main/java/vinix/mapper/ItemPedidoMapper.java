package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vinix.dto.ItemPedidoRequestDTO;
import vinix.dto.ItemPedidoResponseDTO;
import vinix.entities.ItemPedido;

@Mapper(componentModel = "spring")
public interface ItemPedidoMapper {

  ItemPedidoResponseDTO toDTO(ItemPedido entity);

  @Mapping(target = "id",  ignore = true)
  @Mapping(target = "precoUnitario",   ignore = true)
  ItemPedido toEntity(ItemPedidoRequestDTO dto);
}
