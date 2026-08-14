package vinix.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vinix.dtos.CategoriaRequestDTO;
import vinix.dtos.CategoriaResponseDTO;
import vinix.entities.Categoria;
import vinix.mapper.CategoriaMapper;
import vinix.repositories.CategoriaRepository;
import vinix.services.exceptions.ExistenteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

  private final CategoriaRepository repository;
  private final CategoriaMapper mapper;

  @Override
  @Transactional(readOnly = true)
  public List<CategoriaResponseDTO> buscarTodos() {
    return repository.findAll().stream().map(mapper::toDto).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public CategoriaResponseDTO buscarPorId(Long id) {
   return mapper.toDto(repository.findById(id).orElseThrow(
       () -> new ResourceNotFoundException(id + " -> Id não encontrado")));
  }

  @Override
  @Transactional
  public CategoriaResponseDTO salvar(CategoriaRequestDTO dto) {
    if(repository.existsByNome(dto.nome())){
      throw new ExistenteException("Ja existe esse nome: " + dto.nome());
    }

    Categoria entity = mapper.toEntity(dto);
    entity = repository.save(entity);
    return mapper.toDto(entity);
  }

  @Override
  @Transactional
  public CategoriaResponseDTO atualizar(Long id, CategoriaRequestDTO dto) {
    Categoria entity = repository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException(id + " -> categoria não encontrada"));

    if (repository.existsByNomeAndIdNot(dto.nome(), id)) {
      throw new ExistenteException("Já existe outra categoria com esse nome: " + dto.nome());
    }

    entity.setNome(dto.nome());
    entity.setDescricao(dto.descricao());

    entity = repository.save(entity);
    return mapper.toDto(entity);
  }
  
  @Override
  @Transactional
  public void deletar(Long id) {
    if (!repository.existsById(id)) {
      throw new ResourceNotFoundException("Categoria não encontrada com o ID: " + id);
    }

    repository.deleteById(id);
  }
}
