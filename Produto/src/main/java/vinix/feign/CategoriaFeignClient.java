package vinix.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "categoria",
    path = "/categorias",
    fallback = CategoriaFeignClientFallbackFactory.class)
public interface CategoriaFeignClient {

  @GetMapping("/{id}")
  ResponseEntity<CategoriaFeignDTO> buscarPorId(@PathVariable Long id);
}
