package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vinix.entities.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findBySku(String sku);
    boolean existsBySku(String sku);
    List<Produto> findByCategoriaIdAndAtivoTrue(Long categoriaId);
    List<Produto> findByAtivoTrueAndEstoqueGreaterThan(Integer estoqueMinimo);
    Optional<Produto> findByIdAndAtivoTrue(Long id);

    @Modifying
    @Query("UPDATE Produto p SET p.estoque = p.estoque - :quantidade " +
        "WHERE p.id = :id AND p.estoque >= :quantidade")
    int abaterEstoque(@Param("id") Long id, @Param("quantidade") Integer quantidade);
}
