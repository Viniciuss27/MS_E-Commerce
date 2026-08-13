package vinix.resources.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(
        ResourceNotFoundException e,
        HttpServletRequest request) {

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Recurso não encontrado",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(EstoqueInsuficienteException.class)
    public ResponseEntity<StandardError> estoqueInsuficiente(
        EstoqueInsuficienteException e,
        HttpServletRequest request) {

        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY; // 422

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Regra de Negócio - Estoque",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ProdutoExistente.class)
    public ResponseEntity<StandardError> produtoExistente(
        ProdutoExistente e,
        HttpServletRequest request) {

        HttpStatus status = HttpStatus.BAD_REQUEST; // 400

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Requisição Inválida",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> globalException(
        Exception e,
        HttpServletRequest request) {

        log.error("Erro interno no servidor no endpoint: {}", request.getRequestURI(), e);

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
        StandardError err = new StandardError(
            Instant.now(),
            status.value(),
            "Erro Interno no Servidor",
            "Ocorreu um erro inesperado, Por favor, tente novamente mais tarde ou contate o suporte.",
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
                "Erro de Validação",
                "Falha ao validar os campos da requisição",
                request.getRequestURI()
        );

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addError(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }
}