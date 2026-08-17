package vinix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entity.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  Optional<Cliente> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
}
