package vinix.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import vinix.entity.Cliente;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
  Optional<Cliente> findByEmail(String email);
  boolean existsByEmail(String email);
  boolean existsByCpf(String cpf);
  boolean existsByEmailAndIdNot(String email, Long id);
}
