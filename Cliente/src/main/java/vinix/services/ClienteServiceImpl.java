package vinix.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinix.dto.ClienteRequestDTO;
import vinix.dto.ClienteResponseDTO;
import vinix.dto.ClienteUpdateDTO;
import vinix.entity.Cliente;
import vinix.mapper.ClienteMapper;
import vinix.repositories.ClienteRepository;
import vinix.services.exceptions.ExistenteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

  private final ClienteRepository repository;
  private final ClienteMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<ClienteResponseDTO> buscarPorTodos() {
    return repository.findAll().stream().map(mapper :: toDTO).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ClienteResponseDTO buscarPorId(Long id) {
    return mapper.toDTO(repository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(id + " -> Id não encontrado")));
  }

  @Override
  @Transactional(readOnly = true)
  public ClienteResponseDTO buscarPorEmail(String email) {
    return mapper.toDTO(repository.findByEmail(email).orElseThrow(
        () -> new ResourceNotFoundException(email + " -> Email não encontrado")));
  }

  @Override
  @Transactional
  public ClienteResponseDTO salvar(ClienteRequestDTO dto) {
    if(repository.existsByEmail(dto.email())){
      throw new ExistenteException(dto.email()  + " -> Email ja existente");
    }

    if(repository.existsByCpf(dto.cpf())){
      throw new ExistenteException(dto.cpf()  + " -> CPF ja existente");
    }

    Cliente entity = mapper.toEntity(dto);
    entity = repository.save(entity);
    return mapper.toDTO(entity);
  }

  @Override
  @Transactional
  public ClienteResponseDTO atualizar(Long id, ClienteUpdateDTO dto) {
    Cliente entity = repository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(id + " -> Id não encontrado"));

    if (repository.existsByEmailAndIdNot(dto.email(), id)) {
      throw new ExistenteException(dto.email() + " -> Email já existente");
    }

    mapper.updateEntityFromDTO(dto, entity);
    entity = repository.save(entity);
    return mapper.toDTO(entity);
  }

  @Override
  @Transactional
  public void deletar(Long id) {
    if(!repository.existsById(id)){
      throw new ResourceNotFoundException(id + " -> Id não encontrado");
    }

    repository.deleteById(id);
  }
}
