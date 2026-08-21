package vinix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import vinix.dto.ItemPedidoRequestDTO;
import vinix.dto.PedidoRequestDTO;
import vinix.dto.PedidoResponseDTO;
import vinix.entities.ItemPedido;
import vinix.entities.Pedido;
import vinix.entities.StatusPedido;
import vinix.feign.ClienteFeignClient;
import vinix.feign.ProdutoFeignClient;
import vinix.feign.dto.ClienteFeignDTO;
import vinix.feign.dto.ProdutoFeignDTO;
import vinix.kafka.KafkaProducerService;
import vinix.mapper.ItemPedidoMapper;
import vinix.mapper.PedidoMapper;
import vinix.repositories.ItemPedidoRepository;
import vinix.repositories.PedidoRepository;
import vinix.services.PedidoServiceImpl;
import vinix.services.exceptions.ResourceNotFoundException;
import vinix.services.exceptions.ServiceIndisponivelException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PedidoServiceImpl - testes Unitários")
public class PedidoServiceImplTest {

  @Mock private PedidoRepository pedidoRepository;
  @Mock private ItemPedidoRepository itemRepository;
  @Mock private ClienteFeignClient clienteFeign;
  @Mock private ProdutoFeignClient produtoFeign;
  @Mock private PedidoMapper pedidoMapper;
  @Mock private ItemPedidoMapper itemMapper;
  @Mock private KafkaProducerService kafkaProducerService;

  @InjectMocks
  private PedidoServiceImpl service;

  private Pedido pedido;
  private PedidoResponseDTO response;
  private PedidoRequestDTO request;
  private ClienteFeignDTO clienteDTO;
  private ProdutoFeignDTO produtoDTO;

  @BeforeEach
  void setUp() {
    //Pedido "pronto" — como se já estivesse salvo no banco
    ItemPedido item = ItemPedido.builder()
        .id(1L)
        .produtoId(10L)
        .quantidade(2)
        .precoUnitario(new BigDecimal("10.00"))
        .build();

    pedido = Pedido.builder()
        .id(1L)
        .clienteId(1L)
        .status(StatusPedido.PEDIDO_CRIADO)
        .total(new BigDecimal("20.00"))
        .build();

    // O item -> pedido — relação bidirecional da entity
    item.setPedido(pedido);
    pedido.getItens().add(item);

    response = new PedidoResponseDTO(1L, 1L,StatusPedido.PEDIDO_CRIADO
        ,List.of(), new BigDecimal("20.00"), null);

    request = new PedidoRequestDTO(1L,
        List.of(new ItemPedidoRequestDTO(10L, 2)));



    clienteDTO = new ClienteFeignDTO(1L, "Maria", "99988-2233");
    produtoDTO = new ProdutoFeignDTO(10L, "Notebook", new BigDecimal("10.00"));
  }

  @Nested
  @DisplayName("buscarPorId")
  class BuscarPorId {

    @Test
    @DisplayName("Deve retornar o pedido quando o ID existir")
    void pedidoIdExiste() {
      when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));
      when(pedidoMapper.toDTO(pedido)).thenReturn(response);

      PedidoResponseDTO resultado = service.buscarPorId(1L);

      assertThat(resultado).isNotNull();
      assertThat(resultado.id()).isEqualTo(1L);
      verify(pedidoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o ID não existir")
    void pedidoIdNaoExiste() {
      when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.buscarPorId(99L))
          .isInstanceOf(ResourceNotFoundException.class);

      verifyNoInteractions(pedidoMapper);
    }
  }

  @Nested
  @DisplayName("criarPedido")
  class CriarPedido {

    @Test
    @DisplayName("Deve criar o pedido quando cliente e produto existirem")
    void pedidoCriado() {
      //Simula os dois Feigns respondendo com sucesso
      when(clienteFeign.buscarPorId(1L)).thenReturn(ResponseEntity.ok(clienteDTO));
      when(produtoFeign.buscarPorId(10L)).thenReturn(ResponseEntity.ok(produtoDTO));
      when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);
      when(pedidoMapper.toDTO(pedido)).thenReturn(response);

      PedidoResponseDTO resultado = service.criarPedido(request);

      assertThat(resultado).isNotNull();
      verify(pedidoRepository).save(any(Pedido.class));
      // Confirma que o evento foi publicado no Kafka
      verify(kafkaProducerService).enviarPedidoCriado(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o cliente não existir")
    void clienteNaoExiste() {
      when(clienteFeign.buscarPorId(1L)).thenReturn(ResponseEntity.notFound().build());

      assertThatThrownBy(() -> service.criarPedido(request))
          .isInstanceOf(ResourceNotFoundException.class);

      verifyNoInteractions(produtoFeign); //Não deve nem tentar buscar produto
      verify(pedidoRepository, never()).save(any()); //Nem salvar
      verifyNoInteractions(kafkaProducerService); //Nem publicar no kafka
    }

    @Test
    @DisplayName("Deve lançar ServiceIndisponivelException quando ms-cliente estiver fora do ar")
    void cienteIndisponivel() {
      when(clienteFeign.buscarPorId(1L))
          .thenReturn(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build());

      assertThatThrownBy(() -> service.criarPedido(request))
          .isInstanceOf(ServiceIndisponivelException.class);

      verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o produto não existir")
    void produtoNaoExiste() {
      when(clienteFeign.buscarPorId(1L)).thenReturn(ResponseEntity.ok(clienteDTO));
      when(produtoFeign.buscarPorId(10L)).thenReturn(ResponseEntity.notFound().build());

      assertThatThrownBy(() -> service.criarPedido(request))
          .isInstanceOf(ResourceNotFoundException.class);

      verify(pedidoRepository, never()).save(any());
      verifyNoInteractions(kafkaProducerService);
    }
  }

  @Nested
  @DisplayName("cancelarPedido")
  class CancelarPedido {

    @Test
    @DisplayName("Deve cancelar quando o pedido existir")
    void CancelarQuandoExistir() {
      when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

      service.cancelarPedido(1L);

      assertThat(pedido.getStatus()).isEqualTo(StatusPedido.CANCELADO);
      verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando o pedido não existir")
    void QuandoNaoExistir() {
      when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> service.cancelarPedido(99L))
          .isInstanceOf(ResourceNotFoundException.class);

      verify(pedidoRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("atualizarStatus")
  class AtualizarStatus {

    @Test
    @DisplayName("Deve atualizar o status quando o pedido existir")
    void atualizarStatus() {
      when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedido));

      service.atualizarStatus(1L, StatusPedido.ESTOQUE_RESERVADO);

      assertThat(pedido.getStatus()).isEqualTo(StatusPedido.ESTOQUE_RESERVADO);
      verify(pedidoRepository).save(pedido);
    }
  }
}