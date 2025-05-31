package com.trocspace_backend.controller;

import com.trocspace_backend.dto.PasswordResetDTO;
import com.trocspace_backend.dto.UserLoginDTO;
import com.trocspace_backend.dto.UserRegisterDTO;
import com.trocspace_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // Endpoint pour l'enregistrement
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody UserRegisterDTO userRegisterDTO) {
        return authService.register(userRegisterDTO);
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        // Vérifier si le token est fourni
        if (token == null || token.isEmpty()) {
            return new ResponseEntity<>("Token manquant", HttpStatus.BAD_REQUEST);
        }

        // Appeler le service de vérification
        return authService.verifyEmail(token);
    }


    // Endpoint pour la connexion
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserLoginDTO userLoginDTO) {
        // Appel de la méthode authenticate et retour de la réponse complète (avec statut HTTP)
        return authService.authenticate(userLoginDTO);
    }

    // Endpoint pour demander une réinitialisation de mot de passe
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return authService.forgotPassword(email);
    }

    // Endpoint pour vérifier le jeton de réinitialisation
    @GetMapping("/reset-password/verify")
    public ResponseEntity<String> verifyResetToken(@RequestParam String token) {
        return authService.verifyResetToken(token);
    }

    // Endpoint pour réinitialiser le mot de passe
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetDTO passwordResetDTO) {
        return authService.resetPassword(passwordResetDTO);
    }


}
