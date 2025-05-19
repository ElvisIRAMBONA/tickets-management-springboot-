package com.evenement.api.repository;

import com.evenement.api.model.APP_USER;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<APP_USER, Long> {
    Optional<APP_USER> findByUsername(String username);
}
