package com.trocspace_backend.controller;

import com.trocspace_backend.dto.UserDTO;
import com.trocspace_backend.model.User;
import com.trocspace_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    private final UserService userService;

    @Autowired
    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers().stream()
                .filter(user -> !user.getRole().getName().equalsIgnoreCase("admin")) // Exclure les admins
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRawTelephone(),
                        user.getRole(),
                        user.getVerification(),
                        null, // Suppression de getSelfieUploadDate
                        user.isEnabled(),
                        user.isVerified(),
                        user.isEmailVerified()
                )) // Conversion complète vers UserDTO
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-selfie")
    public ResponseEntity<List<UserDTO>> getUsersWithSelfie() {
        List<UserDTO> users = userService.findAllUsers().stream()
                .filter(user -> user.getVerification() != null && user.getVerification().getSelfieCarteIdentite() != null) // Vérifie si un selfie est soumis
                .filter(user -> !user.getRole().getName().equalsIgnoreCase("admin")) // Exclure les admins
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRawTelephone(),
                        user.getRole(),
                        user.getVerification(),
                        null, // Suppression de getSelfieUploadDate
                        user.isEnabled(),
                        user.isVerified(),
                        user.isEmailVerified()
                )) // Conversion complète vers UserDTO
                .toList();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/with-selfie-unverified")
    public ResponseEntity<List<UserDTO>> getUsersWithSelfieUnverified() {
        List<UserDTO> users = userService.findAllUsers().stream()
                .filter(user -> user.getVerification() != null && user.getVerification().getSelfieCarteIdentite() != null) // Vérifie si un selfie est soumis
                .filter(user -> !user.getRole().getName().equalsIgnoreCase("admin")) // Exclure les admins
                .filter(user -> !user.isVerified()) // Filtrer les utilisateurs non vérifiés
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRawTelephone(),
                        user.getRole(),
                        user.getVerification(),
                        null, // Suppression de getSelfieUploadDate
                        user.isEnabled(),
                        user.isVerified(),
                        user.isEmailVerified()
                )) // Conversion complète vers UserDTO
                .toList();

        return ResponseEntity.ok(users);
    }


    @GetMapping("/verified")
    public ResponseEntity<List<UserDTO>> getVerifiedUsers() {
        List<UserDTO> users = userService.findAllUsers().stream()
                .filter(User::isVerified)  // Filtrer les utilisateurs vérifiés
                .filter(user -> !user.getRole().getName().equalsIgnoreCase("admin"))  // Exclure les admins
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getNom(),
                        user.getPrenom(),
                        user.getEmail(),
                        user.getRawTelephone(),
                        user.getRole(),
                        user.getVerification(),
                        null, // Suppression de getSelfieUploadDate
                        user.isEnabled(),
                        user.isVerified(),
                        user.isEmailVerified()
                )) // Conversion complète vers UserDTO
                .toList();

        return ResponseEntity.ok(users);
    }


    // Activer ou désactiver un utilisateur
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleUserStatus(@PathVariable Long id) {
        User user = validateUserExists(id);
        user.setEnabled(!user.isEnabled());
        userService.save(user);
        String status = user.isEnabled() ? "activé" : "désactivé";
        return ResponseEntity.ok("Le compte de l'utilisateur a été " + status + ".");
    }

    // Marquer un utilisateur comme vérifié après validation de l'identité
    @PutMapping("/{id}/verify")
    public ResponseEntity<String> verifyUser(@PathVariable Long id) {
        User user = validateUserExists(id);
        if (user.getVerification() == null || user.getVerification().getSelfieCarteIdentite() == null) {
            return ResponseEntity.badRequest().body("Le selfie de carte d'identité n'a pas été soumis.");
        }
        user.setVerified(true);
        userService.save(user);
        return ResponseEntity.ok("L'utilisateur a été marqué comme vérifié.");
    }

    // Retirer la vérification d'un utilisateur
    @PutMapping("/{id}/unverify")
    public ResponseEntity<String> unverifyUser(@PathVariable Long id) {
        User user = validateUserExists(id);
        if (!user.isVerified()) {
            return ResponseEntity.badRequest().body("L'utilisateur n'est pas encore vérifié.");
        }
        user.setVerified(false); // Désactive la vérification
        userService.save(user); // Sauvegarde l'utilisateur mis à jour
        return ResponseEntity.ok("La vérification de l'utilisateur a été retirée.");
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        User user = validateUserExists(id);
        userService.deleteUserById(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    // Méthode utilitaire pour valider l'existence d'un utilisateur
    private User validateUserExists(Long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé."); // Exception à personnaliser si nécessaire
        }
        return user;
    }
}
