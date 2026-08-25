package vinix.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinix.dto.ItemPedidoRequestDTO;
import vinix.dto.PedidoRequestDTO;
import vinix.dto.PedidoResponseDTO;
import vinix.entities.ItemPedido;
import vinix.entities.Pedido;
import vinix.entities.StatusPedido;
import vinix.events.ItemPedidoEvent;
import vinix.events.PedidoCriadoEvent;
import vinix.feign.ClienteFeignClient;
import vinix.feign.ProdutoFeignClient;
import vinix.feign.dto.ClienteFeignDTO;
import vinix.feign.dto.ProdutoFeignDTO;
import vinix.kafka.KafkaProducerService;
import vinix.mapper.ItemPedidoMapper;
import vinix.mapper.PedidoMapper;
import vinix.repositories.ItemPedidoRepository;
import vinix.repositories.PedidoRepository;
import vinix.services.exceptions.ResourceNotFoundException;
import vinix.services.exceptions.ServiceIndisponivelException;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PedidoServiceImpl implements PedidoService {

  private final PedidoRepository pedidoRepository;
  private final ItemPedidoRepository itemRepository;
  private final ClienteFeignClient clienteFeign;
  private final ProdutoFeignClient produtoFeign;
  private final PedidoMapper pedidoMapper;
  private final ItemPedidoMapper itemMapper;
  private final KafkaProducerService kafka;

  @Override
  @Transactional(readOnly = true)
  public List<PedidoResponseDTO> buscarTodos() {
    return pedidoRepository.findAll().stream().map(pedidoMapper :: toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PedidoResponseDTO buscarPorId(Long id) {
    return pedidoMapper.toDTO(pedidoRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Pedido não encontrado com Id: " + id)));
  }

  @Override
  @Transactional(readOnly = true)
  public List<PedidoResponseDTO> buscarPorClienteId(Long clienteId) {
    validarClienteExistente(clienteId);
    return pedidoRepository.findByClienteId(clienteId)
        .stream().map(pedidoMapper::toDTO).toList();
  }

  @Override
  @Transactional
  public PedidoResponseDTO criarPedido(PedidoRequestDTO dto) {

    validarClienteExistente(dto.clienteId());

    Pedido pedido = Pedido.builder()
        .clienteId(dto.clienteId())
        .status(StatusPedido.PEDIDO_CRIADO)
        .total(BigDecimal.ZERO)
        .build();

    BigDecimal total = BigDecimal.ZERO;

    for(ItemPedidoRequestDTO itens : dto.itens()) {
      ResponseEntity<ProdutoFeignDTO> response = validarProdutoExistente(itens.produtoId());
      ProdutoFeignDTO produto = response.getBody();

      BigDecimal subtotal = produto.preco()
          .multiply(BigDecimal.valueOf(itens.quantidade()));

      ItemPedido item = new ItemPedido();
      item.setPedido(pedido);
      item.setProdutoId(produto.id());
      item.setQuantidade(itens.quantidade());
      item.setPrecoUnitario(produto.preco());

      pedido.getItens().add(item);

      total = total.add(subtotal);
    }

    pedido.setTotal(total);

    pedido = pedidoRepository.save(pedido);

    List<ItemPedidoEvent> itensEvent = pedido.getItens().stream()
        .map(item -> new ItemPedidoEvent(item.getProdutoId(), item.getQuantidade()))
        .toList();

    PedidoCriadoEvent event = new PedidoCriadoEvent(
        pedido.getId(), pedido.getClienteId(), itensEvent);

    kafka.enviarPedidoCriado(event);

    return pedidoMapper.toDTO(pedido);
  }

  @Override
  @Transactional
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> atualizarStatus(Long id, StatusPedido novoStatus) {
    Pedido pedido = pedidoRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Pedido não encontrado com Id: " + id));

    pedido.setStatus(novoStatus);
    pedidoRepository.save(pedido);
    return null;
  }

  @Override
  @Transactional
  public void cancelarPedido(Long id) {
    Pedido pedido = pedidoRepository.findById(id)
        .orElseThrow(() ->
            new ResourceNotFoundException("Pedido não encontrado com Id: " + id));

    boolean estavaPago = pedido.getStatus() == StatusPedido.PAGO;
    pedido.setStatus(StatusPedido.CANCELADO);
    pedidoRepository.save(pedido);
    if (estavaPago) {
      log.info("Pedido ID: {} cancelado, como já estava pago, o reembolso será processado!", id);
    } else {
      log.info("Pedido ID: {} cancelado!", id);
    }
  }

  private void validarClienteExistente(Long clienteId) {
    ResponseEntity<ClienteFeignDTO> response = clienteFeign.buscarPorId(clienteId);

    if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
      throw new ServiceIndisponivelException(
          "Serviço de cliente está indisponível no momento. Tente novamente mais tarde.");
    }

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new ResourceNotFoundException("Cliente não encontrado com o ID: " + clienteId);
    }
  }

  private ResponseEntity<ProdutoFeignDTO> validarProdutoExistente(Long produtoId) {
    ResponseEntity<ProdutoFeignDTO> response = produtoFeign.buscarPorId(produtoId);

    if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
      throw new ServiceIndisponivelException(
          "Serviço de Produto está indisponível no momento, Tente novamente mais tarde!");
    }

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new ResourceNotFoundException("Produto não encontrado com o ID: " + produtoId);
    }
    return response;
  }
}
