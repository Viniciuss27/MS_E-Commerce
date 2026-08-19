package vinix.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vinix.feign.dto.ProdutoFeignDTO;
import vinix.feign.fallback.ProdutoFeignClientFallbackFactory;

@FeignClient(
    name = "produto",
    path = "/produtos",
    fallbackFactory = ProdutoFeignClientFallbackFactory.class
)
public interface ProdutoFeignClient {

  @GetMapping(value = "/{id}")
  public ResponseEntity<ProdutoFeignDTO> buscarPorId(@PathVariable Long id);
}
