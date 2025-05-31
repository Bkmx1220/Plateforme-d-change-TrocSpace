package com.trocspace_backend.repository;

import com.trocspace_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);  // Méthode déjà existante
    boolean existsByEmail(String email);  // Méthode pour vérifier si l'email existe
}
