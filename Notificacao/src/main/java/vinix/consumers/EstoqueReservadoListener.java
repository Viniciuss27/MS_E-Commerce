package vinix.consumers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vinix.events.EstoqueReservadoEvent;
import vinix.services.NotificacaoService;

@Component
@RequiredArgsConstructor
public class EstoqueReservadoListener {

  private final NotificacaoService service;

  @KafkaListener(topics = "estoque-reservado-topic", groupId = "${spring.kafka.consumer.group-id}")
  public void ouvirEstoqueReservado(EstoqueReservadoEvent event) {
    service.notificarEstoqueReservado(event);
  }

}
