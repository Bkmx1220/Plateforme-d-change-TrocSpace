package com.trocspace_backend.controller;

import com.trocspace_backend.model.User;
import com.trocspace_backend.model.UserVerification;
import com.trocspace_backend.service.JwtService;
import com.trocspace_backend.service.UserService;
import com.trocspace_backend.service.UserVerificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final JwtService jwtService;
    private final UserService userService;
    private final UserVerificationService userVerificationService;

    public UserController(JwtService jwtService, UserService userService, UserVerificationService userVerificationService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.userVerificationService = userVerificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
        }

        User user = userService.getCurrentUserWithRoles(userDetails.getUsername());
        UserVerification verification = userVerificationService.findOrCreateVerification(user);

        return ResponseEntity.ok(buildUserResponse(user, verification, true));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        token = token.trim().replace("Bearer ", "");
        if (!jwtService.isTokenValid(token, jwtService.extractUsername(token))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token invalide ou expiré");
        }

        String emailFromToken = jwtService.extractUsername(token);
        User currentUser = userService.getCurrentUserWithRoles(emailFromToken);

        User user = userService.findUserById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        boolean isAdmin = "ADMIN".equals(currentUser.getRole().getName());
        if (!isAdmin && !currentUser.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Accès interdit");
        }

        UserVerification verification = userVerificationService.findOrCreateVerification(user);
        return ResponseEntity.ok(buildUserResponse(user, verification, isAdmin));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody User userDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails userDetailsPrincipal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utilisateur non connecté");
        }

        User user = userService.getCurrentUserWithRoles(userDetailsPrincipal.getUsername());
        updateUserDetails(user, userDetails);

        userService.save(user);
        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "nom", user.getNom(),
                "prenom", user.getPrenom(),
                "email", user.getEmail()
        ));
    }

    private Map<String, Object> buildUserResponse(User user, UserVerification verification, boolean isAdmin) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());
        response.put("telephone", user.getRawTelephone());
        response.put("isVerified", user.isVerified());
        response.put("photoProfil", verification.getPhotoProfil() != null
                ? "http://localhost:8080/api/verification/image/" + verification.getPhotoProfil()
                : "");

        if (isAdmin && verification.getSelfieCarteIdentite() != null) {
            response.put("selfieCarteIdentite", "http://localhost:8080/api/verification/image/" + verification.getSelfieCarteIdentite());
        }

        response.put("role", user.getRole().getName());
        response.put("enabled", user.isEnabled() ? "Activé" : "Désactivé");

        return response;
    }

    private void updateUserDetails(User user, User userDetails) {
        if (userDetails.getNom() != null) {
            user.setNom(userDetails.getNom());
        }
        if (userDetails.getPrenom() != null) {
            user.setPrenom(userDetails.getPrenom());
        }
    }
}
