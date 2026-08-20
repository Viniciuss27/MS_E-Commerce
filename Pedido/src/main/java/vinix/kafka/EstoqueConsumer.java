package vinix.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vinix.entities.Pedido;
import vinix.entities.StatusPedido;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;
import vinix.repositories.PedidoRepository;
import vinix.services.exceptions.ResourceNotFoundException;

@Slf4j
@Component
@RequiredArgsConstructor
public class EstoqueConsumer {

    private final PedidoRepository pedidoRepository;

    @KafkaListener(
        topics = "estoque-reservado-topic",
        groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void ouvirEstoqueReservado(EstoqueReservadoEvent event) {
        log.info("Evento 'EstoqueReservadoEvent' recebido para o Pedido ID: {}",
            event.pedidoId());

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Pedido não encontrado com ID: " + event.pedidoId()));

        pedido.setStatus(StatusPedido.ESTOQUE_RESERVADO);
        pedidoRepository.save(pedido);

        log.info("Pedido ID: {} atualizado para ESTOQUE_RESERVADO", event.pedidoId());

        //lugar de publicar um evento pro ms-notificacao avisar o cliente que o pedido está progredindo
    }

    @KafkaListener(
        topics = "estoque-falhou-topic",
        groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void ouvirEstoqueFalhou(EstoqueFalhouEvent event) {
        log.warn("Evento 'EstoqueFalhouEvent' recebido para o Pedido ID: {}. Motivo: {}",
            event.pedidoId(), event.motivo());

        Pedido pedido = pedidoRepository.findById(event.pedidoId())
            .orElseThrow(() -> new ResourceNotFoundException(
                "Pedido não encontrado com ID: " + event.pedidoId()));

        //cancela automaticamente
        pedido.setStatus(StatusPedido.CANCELADO);
        pedidoRepository.save(pedido);

        log.warn("Pedido ID: {} CANCELADO automaticamente por falta de estoque",
            event.pedidoId());
    }
}