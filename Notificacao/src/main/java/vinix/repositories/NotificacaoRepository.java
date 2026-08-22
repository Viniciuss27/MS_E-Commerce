package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entities.Notificacao;

import java.util.List;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByPedidoId(Long pedidoId);
}