package vinix.services;

import vinix.dto.NotificacaoResponseDTO;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;

import java.util.List;

public interface NotificacaoService {
    void notificarEstoqueReservado(EstoqueReservadoEvent event);
    void notificarEstoqueFalhou(EstoqueFalhouEvent event);
    List<NotificacaoResponseDTO> buscarPorPedidoId(Long pedidoId);
}