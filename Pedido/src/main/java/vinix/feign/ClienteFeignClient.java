package vinix.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vinix.feign.dto.ClienteFeignDTO;
import vinix.feign.fallback.ClienteFeignClientFallbackFactory;

@FeignClient(
    name = "cliente",
    path = "/clientes",
    fallbackFactory = ClienteFeignClientFallbackFactory.class
)
public interface ClienteFeignClient {
  @GetMapping(value = "/{id}")
  public ResponseEntity<ClienteFeignDTO> buscarPorId(@PathVariable Long id);
}
