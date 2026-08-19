package vinix.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import vinix.feign.ClienteFeignClient;

@Component
@Slf4j
public class ClienteFeignClientFallbackFactory implements FallbackFactory<ClienteFeignClient> {

  @Override
  public ClienteFeignClient create(Throwable cause) {
    return (Long id) -> {
      log.error("Não foi possível buscar o Cliente ID: {}, Motivo: {}",
          id, cause.getMessage());
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    };
  }
}
