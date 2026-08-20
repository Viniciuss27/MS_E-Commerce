package vinix.resource;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import vinix.dto.PedidoRequestDTO;
import vinix.dto.PedidoResponseDTO;
import vinix.entities.StatusPedido;
import vinix.services.PedidoService;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/pedidos")
public class PedidoResource {

  private final PedidoService service;

  @GetMapping
  public ResponseEntity<List<PedidoResponseDTO>> buscarTodos() {
    return ResponseEntity.ok(service.buscarTodos());
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Long id) {
    return ResponseEntity.ok(service.buscarPorId(id));
  }

  @GetMapping(value = "/cliente/{clienteId}")
  public ResponseEntity<List<PedidoResponseDTO>> buscarPorClienteId(@PathVariable Long clienteId) {
    return ResponseEntity.ok(service.buscarPorClienteId(clienteId));
  }

  @PutMapping("/{id}/status/{novoStatus}")
  public ResponseEntity<Void> atualizarStatus(
      @PathVariable Long id, @PathVariable StatusPedido novoStatus) {

    service.atualizarStatus(id, novoStatus);
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<PedidoResponseDTO> criarPedido(@Valid @RequestBody PedidoRequestDTO dto) {
    PedidoResponseDTO pedidoSalvo = service.criarPedido(dto);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}").buildAndExpand(pedidoSalvo.id()).toUri();

    return ResponseEntity.created(uri).body(pedidoSalvo);
  }

  @PutMapping("/{id}/cancelar")
  public ResponseEntity<Void> cancelarPedido(@PathVariable Long id) {
    service.cancelarPedido(id);
    return ResponseEntity.noContent().build();
  }
}
