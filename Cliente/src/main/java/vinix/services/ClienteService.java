package vinix.services;

import vinix.dto.ClienteRequestDTO;
import vinix.dto.ClienteResponseDTO;
import vinix.dto.ClienteUpdateDTO;

import java.util.List;

public interface ClienteService {

  public List<ClienteResponseDTO> buscarPorTodos();

  public ClienteResponseDTO buscarPorId(Long id);

  public ClienteResponseDTO buscarPorEmail(String email);

  public ClienteResponseDTO salvar(ClienteRequestDTO dto);

  public ClienteResponseDTO atualizar(Long id, ClienteUpdateDTO dto);

  public void deletar(Long id);


}
