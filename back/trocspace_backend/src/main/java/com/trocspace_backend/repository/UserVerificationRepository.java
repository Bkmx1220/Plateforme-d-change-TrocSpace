package com.trocspace_backend.repository;

import com.trocspace_backend.model.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserVerificationRepository extends JpaRepository<UserVerification, Long> {
    UserVerification findByUserId(Long userId);
}
