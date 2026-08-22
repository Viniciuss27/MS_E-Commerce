package vinix.services.exceptions;

public class ServiceIndisponivelException extends RuntimeException {
  public ServiceIndisponivelException(String message) {
    super(message);
  }
}
