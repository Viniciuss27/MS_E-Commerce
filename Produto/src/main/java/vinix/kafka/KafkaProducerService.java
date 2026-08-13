package vinix.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPICO_ESTOQUE_RESERVADO = "estoque-reservado-topic";
    private static final String TOPICO_ESTOQUE_FALHOU = "estoque-falhou-topic";

    public void enviarEstoqueReservado(EstoqueReservadoEvent event) {
        log.info("Enviando evento de Estoque Reservado com sucesso para o Pedido ID: {}",
            event.pedidoId());

        kafkaTemplate.send(TOPICO_ESTOQUE_RESERVADO, event.pedidoId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Falha ao publicar EstoqueReservadoEvent para o Pedido ID: {}",
                        event.pedidoId(), ex);
                } else {
                    log.debug("EstoqueReservadoEvent confirmado no offset {}",
                        result.getRecordMetadata().offset());
                }
            });
    }

    public void enviarEstoqueFalhou(EstoqueFalhouEvent event) {
        log.warn("Enviando evento de Falha de Estoque para o Pedido ID: {}. Motivo: {}",
            event.pedidoId(), event.motivo());

        kafkaTemplate.send(TOPICO_ESTOQUE_FALHOU, event.pedidoId().toString(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Falha ao publicar EstoqueFalhouEvent para o Pedido ID: {}",
                        event.pedidoId(), ex);
                }
            });
    }
}