package com.nmit.canteen.repository;

import com.nmit.canteen.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false) // never expose users
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
