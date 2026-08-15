package vinix.resources.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import vinix.services.exceptions.ExistenteException;
import vinix.services.exceptions.ResourceNotFoundException;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class ResourceExceptionHandler {


  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<StandardError> resourceNotFound(
      ResourceNotFoundException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND; //404

    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Recurso não encontrado",
        e.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler({ExistenteException.class})
  public ResponseEntity<StandardError> businessError(
      RuntimeException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.BAD_REQUEST; //400

    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Regra de negócio violada",
        e.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<StandardError> dataIntegrity(
      DataIntegrityViolationException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.CONFLICT;//409

    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Violação de integridade de dados",
        "Não é possível realizar esta operação pois o registro possui relacionamentos vinculados.",
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> validation(
      MethodArgumentNotValidException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422

    ValidationError err = new ValidationError(
        Instant.now(),
        status.value(),
        "Erro de validação nos campos",
        "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.",
        request.getRequestURI()
    );

    for (FieldError f : e.getBindingResult().getFieldErrors()) {
      err.addError(f.getField(), f.getDefaultMessage());
    }

    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardError> globalException(
      Exception e, HttpServletRequest request) {

    log.error("Erro inesperado no servidor ao acessar {}: ",
        request.getRequestURI(), e);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Erro Interno no Servidor",
        "Ocorreu um erro inesperado, Por favor, tente novamente mais tarde",
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }
}