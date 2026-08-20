package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entities.Pedido;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
  List<Pedido> findByClienteId(Long clientId);
}
