package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entities.Produto;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    List<Produto> findByAtivoTrueAndEstoqueGreaterThan(Integer estoqueMinimo);
    Optional<Produto> findByIdAndAtivoTrue(Long id);
}
