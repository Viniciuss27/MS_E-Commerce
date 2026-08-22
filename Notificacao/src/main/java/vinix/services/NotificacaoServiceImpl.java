package vinix.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinix.dto.NotificacaoResponseDTO;
import vinix.entities.Notificacao;
import vinix.entities.TipoNotificacao;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;
import vinix.mapper.NotificacaoMapper;
import vinix.repositories.NotificacaoRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

  private final NotificacaoRepository repository;
  private final NotificacaoMapper mapper;

  @Override
  @Transactional
  public void notificarEstoqueReservado(EstoqueReservadoEvent event) {
    log.info("Estoque reservado: pedido número {}, {}",
        event.pedidoId(), event.mensagem());

    Notificacao notificacao = Notificacao.builder()
        .pedidoId(event.pedidoId())
        .tipo(TipoNotificacao.ESTOQUE_RESERVADO)
        .mensagem(event.mensagem())
        .build();

    repository.save(notificacao);
  }

  @Override
  @Transactional
  public void notificarEstoqueFalhou(EstoqueFalhouEvent event) {
    log.warn("Não foi possível reservar o estoque do pedido número {}, motivo: {}",
        event.pedidoId(), event.motivo());

    Notificacao notificacao = Notificacao.builder()
        .pedidoId(event.pedidoId())
        .tipo(TipoNotificacao.ESTOQUE_FALHOU)
        .mensagem(event.motivo())
        .build();

    repository.save(notificacao);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NotificacaoResponseDTO> buscarPorPedidoId(Long pedidoId) {
    return repository.findByPedidoId(pedidoId).stream()
        .map(mapper::toDTO)
        .toList();
  }
}