package vinix.services;

import vinix.dtos.CategoriaRequestDTO;
import vinix.dtos.CategoriaResponseDTO;

import java.util.List;

public interface CategoriaService {

  List<CategoriaResponseDTO> buscarTodos();

  CategoriaResponseDTO buscarPorId(Long id);

  CategoriaResponseDTO salvar(CategoriaRequestDTO dto);

  CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto);

  void deletar(Long id);
}
