package vinix.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import vinix.dto.ProdutoRequestDTO;
import vinix.dto.ProdutoResponseDTO;
import vinix.services.ProdutoService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = "/produtos")
@RequiredArgsConstructor
public class ProdutoResource {

  private final ProdutoService service;

  @GetMapping
  public ResponseEntity<List<ProdutoResponseDTO>> buscarTodos() {
    List<ProdutoResponseDTO> list = service.buscarTodos();
    return ResponseEntity.ok(list);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
    ProdutoResponseDTO dto = service.buscarPorId(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping(value = "/categoria/{categoriaId}")
  public ResponseEntity<List<ProdutoResponseDTO>> buscarPorCategoria(
      @PathVariable Long categoriaId) {

    List<ProdutoResponseDTO> list = service.buscarPorCategoria(categoriaId);
    return ResponseEntity.ok(list);
  }

  @GetMapping(value = "/disponiveis")
  public ResponseEntity<List<ProdutoResponseDTO>> buscarDisponiveis() {
    List<ProdutoResponseDTO> list = service.buscarDisponiveis();
    return ResponseEntity.ok(list);
  }

  @PostMapping
  public ResponseEntity<ProdutoResponseDTO> salvar(
      @RequestBody @Valid ProdutoRequestDTO dto) {

    ProdutoResponseDTO produtoSalvo = service.salvar(dto);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(produtoSalvo.id()).toUri();

    return ResponseEntity.created(uri).body(produtoSalvo);
  }

  @PutMapping(value = "/{id}")
  public ResponseEntity<ProdutoResponseDTO> atualizar(
      @PathVariable Long id, @RequestBody @Valid ProdutoRequestDTO dto) {

    ProdutoResponseDTO produtoAtualizado = service.atualizar(id, dto);
    return ResponseEntity.ok(produtoAtualizado);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> desativar(@PathVariable Long id) {
    service.desativar(id);
    return ResponseEntity.noContent().build();
  }
}