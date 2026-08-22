package vinix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vinix.dto.NotificacaoResponseDTO;
import vinix.entities.Notificacao;
import vinix.entities.TipoNotificacao;
import vinix.events.EstoqueFalhouEvent;
import vinix.events.EstoqueReservadoEvent;
import vinix.mapper.NotificacaoMapper;
import vinix.repositories.NotificacaoRepository;
import vinix.services.NotificacaoServiceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificacaoServiceImpl - testes Unitários")
public class NotificacaoServiceImplTest {

    @Mock
    private NotificacaoRepository repository;

    @Mock
    private NotificacaoMapper mapper;

    @InjectMocks
    private NotificacaoServiceImpl service;

    private EstoqueReservadoEvent eventoReservado;
    private EstoqueFalhouEvent eventoFalhou;
    private Notificacao notificacao;
    private NotificacaoResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        eventoReservado = new EstoqueReservadoEvent(
            1L, true, "Estoque reservado com sucesso!");

        eventoFalhou = new EstoqueFalhouEvent(
            1L, "Estoque insuficiente");

        notificacao = Notificacao.builder()
            .id(1L)
            .pedidoId(1L)
            .tipo(TipoNotificacao.ESTOQUE_RESERVADO)
            .mensagem("Estoque reservado com sucesso!")
            .build();

        responseDTO = new NotificacaoResponseDTO(
            1L, 1L, TipoNotificacao.ESTOQUE_RESERVADO,
            "Estoque reservado com sucesso!", null
        );
    }

    @Nested
    @DisplayName("notificarEstoqueReservado")
    class NotificarEstoqueReservado {

        @Test
        @DisplayName("Deve salvar a notificação de estoque reservado")
        void salvaNotificacao() {
            service.notificarEstoqueReservado(eventoReservado);

            verify(repository).save(argThat(n ->
                n.getPedidoId().equals(1L) &&
                n.getTipo() == TipoNotificacao.ESTOQUE_RESERVADO &&
                n.getMensagem().equals("Estoque reservado com sucesso!")
            ));
        }
    }

    @Nested
    @DisplayName("notificarEstoqueFalhou")
    class NotificarEstoqueFalhou {

        @Test
        @DisplayName("Deve salvar a notificação de falha de estoque")
        void SalvaNotificacao() {
            service.notificarEstoqueFalhou(eventoFalhou);

            verify(repository).save(argThat(n ->
                n.getPedidoId().equals(1L) &&
                n.getTipo() == TipoNotificacao.ESTOQUE_FALHOU &&
                n.getMensagem().equals("Estoque insuficiente")
            ));
        }
    }

    @Nested
    @DisplayName("buscarPorPedidoId")
    class BuscarPorPedidoId {

        @Test
        @DisplayName("Deve retornar as notificações do pedido")
        void retornaNotificacoes() {
            when(repository.findByPedidoId(1L)).thenReturn(List.of(notificacao));
            when(mapper.toDTO(notificacao)).thenReturn(responseDTO);

            List<NotificacaoResponseDTO> resultado = service.buscarPorPedidoId(1L);

            assertThat(resultado).hasSize(1);
            assertThat(resultado.get(0).pedidoId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Deve retornar lista vazia quando não houver notificações")
        void retornaListaVazia() {
            when(repository.findByPedidoId(2L)).thenReturn(List.of());

            List<NotificacaoResponseDTO> resultado = service.buscarPorPedidoId(2L);

            assertThat(resultado).isEmpty();
            verifyNoInteractions(mapper);
        }
    }
}