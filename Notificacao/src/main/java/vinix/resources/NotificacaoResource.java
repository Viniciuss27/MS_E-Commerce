package vinix.resources;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vinix.dto.NotificacaoResponseDTO;
import vinix.services.NotificacaoService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notificacoes")
public class NotificacaoResource {

    private final NotificacaoService service;

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<List<NotificacaoResponseDTO>> buscarPorPedidoId(
        @PathVariable Long pedidoId) {

        return ResponseEntity.ok(service.buscarPorPedidoId(pedidoId));
    }
}