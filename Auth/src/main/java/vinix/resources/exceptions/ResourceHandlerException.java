package vinix.resources.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vinix.services.exceptions.DuplicateEmailException;
import vinix.services.exceptions.ResourceNotFoundException;


import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class ResourceHandlerException {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<StandardError> resourceNotFound(
      ResourceNotFoundException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    StandardError err = StandardError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("Resource not found").message(e.getMessage())
        .path(request.getRequestURI()).build();

    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<StandardError> userNotFound(
      UserNotFoundException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.NOT_FOUND;

    StandardError err = StandardError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("User not found").message(e.getMessage())
        .path(request.getRequestURI()).build();

    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<StandardError> usernameNotFound(
      UsernameNotFoundException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    StandardError err = StandardError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("Unauthorized").message("Usuário ou senha inválidos")
        .path(request.getRequestURI()).build();

    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<StandardError> badCredentials(
      BadCredentialsException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNAUTHORIZED;

    StandardError err = StandardError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("Unauthorized").message("Usuário ou senha inválidos")
        .path(request.getRequestURI()).build();

    return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<StandardError> duplicateEmail(
      DuplicateEmailException e,
      HttpServletRequest request) {

      HttpStatus status = HttpStatus.CONFLICT;

      StandardError err = StandardError.builder()
          .timestamp(Instant.now()).status(status.value())
          .message(e.getMessage()).error("Email já cadastrado")
          .path(request.getRequestURI()).build();

      return ResponseEntity.status(status).body(err);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<StandardError> globalException(
      Exception e,
      HttpServletRequest request) {

    log.error("Erro inesperado no servidor ao acessar {}: ",
        request.getRequestURI(), e);

    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    StandardError err = StandardError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("Erro Interno no Servidor").message("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde")
        .path(request.getRequestURI()).build();

    return ResponseEntity.status(status).body(err);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationError> validation(
      MethodArgumentNotValidException e,
      HttpServletRequest request) {

    HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

    ValidationError err = ValidationError.builder()
        .timestamp(Instant.now()).status(status.value())
        .error("Validation exception").message("Erro na validação dos campos")
        .path(request.getRequestURI()).build();

    for (FieldError f : e.getBindingResult().getFieldErrors()) {
      err.addErro(f.getField(), f.getDefaultMessage());
    }

    return ResponseEntity.status(status).body(err);
  }
}