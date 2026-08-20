package vinix.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vinix.events.PedidoCriadoEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPICO_PEDIDO_CRIADO = "pedido-criado-topic";

    public void enviarPedidoCriado(PedidoCriadoEvent event) {
        log.info("Publicando PedidoCriadoEvent para o Pedido ID: {}", event.pedidoId());

        kafkaTemplate.send(TOPICO_PEDIDO_CRIADO, event.pedidoId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Falha ao publicar PedidoCriadoEvent para o Pedido ID: {}",
                        event.pedidoId(), ex);
                } else {
                    log.debug("PedidoCriadoEvent confirmado no offset {}",
                        result.getRecordMetadata().offset());
                }
            });
    }
}