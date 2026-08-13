package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinix.entities.EventoProcessado;

@Repository
public interface EventoProcessadoRepository extends JpaRepository<EventoProcessado, Long> {
    boolean existsByPedidoId(Long pedidoId);
}