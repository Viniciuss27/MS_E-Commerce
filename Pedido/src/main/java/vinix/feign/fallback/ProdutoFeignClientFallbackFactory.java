package vinix.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vinix.feign.ProdutoFeignClient;

@Component
@Slf4j
public class ProdutoFeignClientFallbackFactory implements FallbackFactory<ProdutoFeignClient> {

  @Override
  public ProdutoFeignClient create(Throwable cause) {
    return (Long id) -> {
      log.error("Não foi possivel buscar o Produto: {}, motivo {}",
          id, cause.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    };
  }
}
