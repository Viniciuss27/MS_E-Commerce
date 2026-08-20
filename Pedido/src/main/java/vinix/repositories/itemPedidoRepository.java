package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entities.ItemPedido;

public interface itemPedidoRepository extends JpaRepository<ItemPedido, Long> {
}
