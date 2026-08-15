package vinix.resources.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vinix.services.exceptions.ResourceNotFoundException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ExceptionHandler {

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<StandardError> dataIntegrityViolation(
      DataIntegrityViolationException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.CONFLICT; // 409

    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Conflito de Integridade",
        "Não é possível excluir esta categoria pois existem produtos vinculados a ela.",
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  package vinix.resources.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf me.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import vinix.services.exceptions.EstoqueInsuficienteException;
import vinix.services.exceptions.ProdutoExistente;
import vinix.services.exceptions.ResourceNotFoundException;

import java.time.Instant;


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

  // 400 - Produto/Regra de Negócio com Conflito ou Erro
  @ExceptionHandler({ProdutoExistente.class,
      IllegalArgumentException.class, EstoqueInsuficienteException.class})
  public ResponseEntity<StandardError> businessError(RuntimeException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;
    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Regra de negócio violada",
        e.getMessage(),
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  // 409 - Violação de Integridade do Banco (Ex: Excluir Categoria com Produtos vinculados)
  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<StandardError> dataIntegrity(DataIntegrityViolationException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.CONFLICT;
    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Violação de integridade de dados",
        "Não é possível realizar esta operação pois o registro possui relacionamentos vinculados.",
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }

  // 422 - Erros de Validação do Bean Validation (@Valid / @NotBlank / @Size)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422
    ValidationError err = new ValidationError(
        Instant.now(),
        status.value(),
        "Erro de validação nos campos",
        "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.",
        request.getRequestURI()
    );

    // Captura todos os erros de campos no DTO e adiciona na lista 'errors'
    for (FieldError f : e.getBindingResult().getFieldErrors()) {
      err.addError(f.getFieldName(), f.getDefaultMessage());
    }

    return ResponseEntity.status(status).body(err);
  }

  // 500 - Captura qualquer erro inesperado do Servidor
  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardError> globalException(Exception e, HttpServletRequest request) {
    log.error("Erro inesperado no servidor ao acessar {}: ", request.getRequestURI(), e);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
    StandardError err = new StandardError(
        Instant.now(),
        status.value(),
        "Erro Interno no Servidor",
        "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
        request.getRequestURI()
    );
    return ResponseEntity.status(status).body(err);
  }
}
}
