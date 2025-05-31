package com.trocspace_backend.service;

import com.trocspace_backend.model.User;
import com.trocspace_backend.model.UserVerification;
import com.trocspace_backend.repository.UserVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserVerificationService {

    private final UserVerificationRepository userVerificationRepository;

    @Autowired
    public UserVerificationService(UserVerificationRepository userVerificationRepository) {
        this.userVerificationRepository = userVerificationRepository;
    }

    public UserVerification findOrCreateVerification(User user) {
        UserVerification verification = userVerificationRepository.findByUserId(user.getId());
        if (verification == null) {
            verification = new UserVerification();
            verification.setUser(user);
        }
        return verification;
    }

    public void saveVerification(UserVerification verification) {
        userVerificationRepository.save(verification);
    }

    public void deleteVerification(UserVerification verification) {
        userVerificationRepository.delete(verification);
    }
}
