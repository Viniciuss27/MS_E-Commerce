package vinix.resources.exceptions;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@SuperBuilder
public class ValidationError extends StandardError{

  @Builder.Default
  private List<FieldMessage> erros = new ArrayList<>();

  public void addErro(String fieldName, String message ) {
    erros.add(new FieldMessage(fieldName ,message));
  }

   public record FieldMessage(String fieldName, String message) {}
}
