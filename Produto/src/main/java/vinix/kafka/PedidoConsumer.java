package vinix.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import vinix.dto.ItemPedidoDTO;
import vinix.entities.EventoProcessado;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;
import vinix.events.PedidoCriadoEvent;
import vinix.repositories.EventoProcessadoRepository;
import vinix.services.ProdutoService;
import vinix.services.exceptions.EstoqueInsuficienteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PedidoConsumer {

    private final ProdutoService produtoService;
    private final KafkaProducerService producerService;
    private final EventoProcessadoRepository eventoProcessadoRepository;

    @KafkaListener(
        topics = "pedido-criado-topic",
        groupId = "${spring.kafka.consumer.group-id:MS_E-Commerce}")
    @Transactional
    public void ouvirPedidoCriado(PedidoCriadoEvent event) {
        log.info("Evento 'PedidoCriadoEvent' recebido no Kafka para o Pedido ID: {}",
            event.pedidoId());

        if (eventoProcessadoRepository.existsByPedidoId(event.pedidoId())) {//Verifica Idempotência
            log.warn("Pedido ID: {} já foi processado anteriormente, Ignorando evento duplicado.",
                event.pedidoId());
            return;
        }

        try {
            List<ItemPedidoDTO> itensDTO = event.itens().stream()
                    .map(item -> new ItemPedidoDTO(item.produtoId(),
                    item.quantidade())).toList();

            produtoService.reservarEstoque(itensDTO);
            eventoProcessadoRepository.save(EventoProcessado.builder()
                    .pedidoId(event.pedidoId()).build());

            EstoqueReservadoEvent sucessoEvent = new EstoqueReservadoEvent(
                    event.pedidoId(), true, "Estoque reservado com sucesso!");
            producerService.enviarEstoqueReservado(sucessoEvent);

        } catch (ResourceNotFoundException | EstoqueInsuficienteException e) {
            log.error("Erro ao reservar estoque para o Pedido ID: {}. Motivo: {}",
                event.pedidoId(), e.getMessage());

            EstoqueFalhouEvent falhaEvent = new EstoqueFalhouEvent(
                    event.pedidoId(), e.getMessage());
            producerService.enviarEstoqueFalhou(falhaEvent);

        } catch (Exception e) {
            log.error("Erro inesperado ao processar evento do Pedido ID: {}",
                event.pedidoId(), e);
            throw e; // Lança novamente para o Kafka re-tentar se for um erro de infra/banco
        }
    }
}