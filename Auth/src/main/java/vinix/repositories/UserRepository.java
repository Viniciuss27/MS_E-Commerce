package vinix.repositories;

import vinix.entities.User;
import java.util.Optional;

public interface UserRepository {
   Optional<User> findByEmail(String email);
}
