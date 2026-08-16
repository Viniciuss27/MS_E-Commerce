package vinix.feigh;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    name = "categoria",
    path = "/categorias",
    fallback = CategoriaFeighClientFallbackFactory.class)
public interface CategoriaFeighClient {

  @GetMapping("/{id}")
  ResponseEntity<CategoriaFeighDTO> buscarPorId(@PathVariable Long id);
}
