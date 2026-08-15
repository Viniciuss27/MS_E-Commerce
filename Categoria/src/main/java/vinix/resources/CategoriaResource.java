package vinix.resources;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vinix.dtos.CategoriaRequestDTO;
import vinix.dtos.CategoriaResponseDTO;
import vinix.services.CategoriaService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/categorias")
public class CategoriaResource {

  private final CategoriaService service;

  @GetMapping
  public ResponseEntity<List<CategoriaResponseDTO>> buscarTodos() {
    List<CategoriaResponseDTO> list = service.buscarTodos();
    return ResponseEntity.ok(list);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoriaResponseDTO> buscarPorId(@PathVariable Long id) {
    CategoriaResponseDTO dto = service.buscarPorId(id);
    return ResponseEntity.ok(dto);
  }

  @PostMapping
  public ResponseEntity<CategoriaResponseDTO> salvar(
      @Valid @RequestBody CategoriaRequestDTO dto) {

    CategoriaResponseDTO responseDTO = service.salvar(dto);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(responseDTO.id())
        .toUri();

    return ResponseEntity.created(uri).body(responseDTO);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoriaResponseDTO> atualizar(
      @PathVariable Long id, @Valid @RequestBody CategoriaRequestDTO dto) {

    CategoriaResponseDTO responseDTO = service.atualizar(id, dto);
    return ResponseEntity.ok(responseDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable Long id) {
    service.deletar(id);
    return ResponseEntity.noContent().build();
  }
}