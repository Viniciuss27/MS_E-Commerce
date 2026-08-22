package vinix.consumers;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import vinix.events.EstoqueFalhouEvent;
import vinix.services.NotificacaoService;

@Component
@RequiredArgsConstructor
public class EstoqueFalhouLinester{

  private final NotificacaoService service;

  @KafkaListener(topics = "estoque-falhou-topic", groupId = "${spring.kafka.consumer.group-id}")
  public void ouvirEstoqueFalhou(EstoqueFalhouEvent event) {
    service.notificarEstoqueFalhou(event);
  }

}