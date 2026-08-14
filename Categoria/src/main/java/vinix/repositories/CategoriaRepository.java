package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vinix.entities.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

  boolean existsByNome(String nome);

  boolean existsByNomeAndIdNot(String nome, Long id);
}