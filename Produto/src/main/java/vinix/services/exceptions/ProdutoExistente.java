package vinix.services.exceptions;

public class ProdutoExistente extends RuntimeException {
  public ProdutoExistente(String message) {
    super(message);
  }
}
