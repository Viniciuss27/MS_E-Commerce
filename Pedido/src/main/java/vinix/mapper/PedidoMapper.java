package vinix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import vinix.dto.PedidoRequestDTO;
import vinix.dto.PedidoResponseDTO;
import vinix.entities.Pedido;

@Mapper(componentModel = "spring", uses = ItemPedidoMapper.class)
//para converter a lista (List<ItemPedidoRequestDTO> itens)
public interface PedidoMapper {

  PedidoResponseDTO toDTO(Pedido entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "total", ignore = true)
  @Mapping(target = "dataCriacao", ignore = true)
  Pedido toEntity(PedidoRequestDTO dto);
}
