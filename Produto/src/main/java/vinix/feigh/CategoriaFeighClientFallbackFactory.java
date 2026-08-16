package vinix.feigh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CategoriaFeighClientFallbackFactory implements FallbackFactory<CategoriaFeighClient> {

  @Override
  public CategoriaFeighClient create(Throwable cause) {
    return (id) -> {
      log.error("Não foi possível buscar a categoria ID: {}. Motivo: {}",
          id, cause.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    };
  }
}
