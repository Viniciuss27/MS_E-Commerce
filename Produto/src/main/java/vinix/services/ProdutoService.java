package vinix.services;


import vinix.dto.ItemPedidoDTO;
import vinix.dto.ProdutoRequestDTO;
import vinix.dto.ProdutoResponseDTO;

import java.util.List;

public interface ProdutoService {

    List<ProdutoResponseDTO> buscarTodos();

    ProdutoResponseDTO buscarPorId(Long id);

    List<ProdutoResponseDTO> buscarPorCategoria(Long categoriaId);

    List<ProdutoResponseDTO> buscarDisponiveis();

    ProdutoResponseDTO salvar(ProdutoRequestDTO dto);

    ProdutoResponseDTO atualizar(Long id, ProdutoRequestDTO dto);

    void desativar(Long id);

    // Método chamado pelo Consumidor Kafka para reservar estoque
    void reservarEstoque(List<ItemPedidoDTO> itens);
}
