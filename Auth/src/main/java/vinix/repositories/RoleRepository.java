package vinix.repositories;

import vinix.entities.User;

import java.util.Optional;

public interface RoleRepository {
  Optional<User> findByName(String name);
}
