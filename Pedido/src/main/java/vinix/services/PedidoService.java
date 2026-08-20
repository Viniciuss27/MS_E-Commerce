package vinix.services;

import vinix.dto.PedidoRequestDTO;
import vinix.dto.PedidoResponseDTO;
import vinix.entities.StatusPedido;

import java.util.List;

public interface PedidoService {

  List<PedidoResponseDTO> buscarTodos();

  PedidoResponseDTO buscarPorId(Long id);

  List<PedidoResponseDTO> buscarPorClienteId(Long clienteId);

  PedidoResponseDTO criarPedido(PedidoRequestDTO dto);

  void atualizarStatus(Long id, StatusPedido novoStatus);

  void cancelarPedido(Long id);
}