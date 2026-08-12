package vinix.services.exceptions;

public class EstoqueInsuficienteException extends RuntimeException {
  public EstoqueInsuficienteException(String message) {
    super(message);
  }
}
