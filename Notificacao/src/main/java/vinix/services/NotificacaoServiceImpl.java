package vinix.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;

@Service
@Slf4j
public class NotificacaoServiceImpl implements NotificacaoService {

  @Override
  public void notificarEstoqueReservado(EstoqueReservadoEvent event) {
    log.info("Estoque reservado: pedido numero{}, {}",
        event.pedidoId(),
        event.mensagem());
  }

  @Override
  public void notificarEstoqueFalhou(EstoqueFalhouEvent event) {
    log.warn("Não foi possivel reservar o Estoque do pedido numero {}, motivo: {}",
        event.pedidoId(),
        event.motivo());
  }
}
