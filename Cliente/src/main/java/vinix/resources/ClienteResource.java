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
import vinix.dto.ClienteRequestDTO;
import vinix.dto.ClienteResponseDTO;
import vinix.dto.ClienteUpdateDTO;
import vinix.services.ClienteServiceImpl;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/clientes")
public class ClienteResource {

  private final ClienteServiceImpl service;

  @GetMapping
  public ResponseEntity<List<ClienteResponseDTO>> buscarPorTodos() {
    return ResponseEntity.ok(service.buscarPorTodos());
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<ClienteResponseDTO> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(service.buscarPorId(id));
  }

  @GetMapping(value = "/email/{email}")
  public ResponseEntity<ClienteResponseDTO> buscarPorEmail(@PathVariable String email) {
    return ResponseEntity.ok(service.buscarPorEmail(email));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ClienteResponseDTO> atualizar(
      @PathVariable Long id, @Valid @RequestBody ClienteUpdateDTO dto) {
    return ResponseEntity.ok(service.atualizar(id, dto));
  }

  @PostMapping
  public ResponseEntity<ClienteResponseDTO> salvar(@Valid @RequestBody ClienteRequestDTO dto) {
    ClienteResponseDTO responseDTO = service.salvar(dto);

    URI uri =  ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(responseDTO.id()).toUri();
    return ResponseEntity.created(uri).body(responseDTO);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deletar(@PathVariable Long id) {
    service.deletar(id);
    return ResponseEntity.noContent().build();
  }
}
