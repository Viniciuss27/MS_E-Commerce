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
        ResourceNotFoundException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND; //404

        StandardError err = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .message(e.getMessage())
            .error("Recurso não encontrado")
            .path(request.getRequestURI()).build();

        return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(ExistenteException.class)
    public ResponseEntity<StandardError> existenterror(
        ExistenteException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST; //400

        StandardError err = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .message(e.getMessage())
            .error("Regra de negócio violada")
            .path(request.getRequestURI()).build();

        return ResponseEntity.status(status).body(err);
        }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<StandardError> dataIntegrity(
        DataIntegrityViolationException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.CONFLICT;//409

        log.error("Violação de integridade de dados: {}", e.getMessage());

        StandardError err = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Violação de integridade de dados")
            .message("Não é possível realizar esta operação pois o registro possui relacionamentos vinculados")
            .path(request.getRequestURI()).build();

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(
        MethodArgumentNotValidException e, HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationError err = ValidationError.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value()) //422
                .error("Validation exception")
                .message("Erro na validação dos campos")
                .path(request.getRequestURI())
                .build();

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
        StandardError err = StandardError.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error("Erro Interno no Servidor")
            .message("Ocorreu um erro inesperado, Por favor, tente novamente mais tarde")
            .path(request.getRequestURI()).build();

        return ResponseEntity.status(status).body(err);
    }
}