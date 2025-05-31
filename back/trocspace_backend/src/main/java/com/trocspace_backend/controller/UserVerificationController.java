package com.trocspace_backend.controller;

import com.trocspace_backend.model.User;
import com.trocspace_backend.model.UserVerification;
import com.trocspace_backend.repository.UserRepository;
import com.trocspace_backend.service.FileService;
import com.trocspace_backend.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/verification")
public class UserVerificationController {

    private final FileService fileService;
    private final UserVerificationService userVerificationService;
    private final UserRepository userRepository;

    @Autowired
    public UserVerificationController(
            FileService fileService,
            UserVerificationService userVerificationService,
            UserRepository userRepository) {
        this.fileService = fileService;
        this.userVerificationService = userVerificationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadVerificationFiles(
            @RequestParam(value = "photoProfil", required = false) MultipartFile photoProfil,
            @RequestParam(value = "selfieCarteIdentite", required = false) MultipartFile selfieCarteIdentite) {

        User user = getAuthenticatedUser();
        UserVerification verification = userVerificationService.findOrCreateVerification(user);

        try {
            // Traitement de la photo de profil
            if (photoProfil != null) {
                if (!fileService.isValidImage(photoProfil)) {
                    return ResponseEntity.badRequest().body("Le fichier photoProfil doit être une image valide.");
                }
                String photoProfilPath = fileService.saveFile(photoProfil);
                if (photoProfilPath != null && !photoProfilPath.isEmpty()) {
                    if (verification.getPhotoProfil() != null && !verification.getPhotoProfil().isEmpty()) {
                        fileService.deleteFile(verification.getPhotoProfil()); // Suppression de l'ancienne photo de profil
                    }
                    verification.setPhotoProfil(photoProfilPath); // Mise à jour du chemin dans la base
                }
            }

            // Traitement du selfie avec carte d'identité
            if (selfieCarteIdentite != null) {
                if (user.isVerified()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Le selfie avec carte d'identité ne peut pas être modifié après vérification du compte.");
                }
                if (!fileService.isValidImage(selfieCarteIdentite)) {
                    return ResponseEntity.badRequest().body("Le fichier selfieCarteIdentite doit être une image valide.");
                }
                String selfiePath = fileService.saveFile(selfieCarteIdentite);
                if (selfiePath != null && !selfiePath.isEmpty()) {
                    if (verification.getSelfieCarteIdentite() != null && !verification.getSelfieCarteIdentite().isEmpty()) {
                        fileService.deleteFile(verification.getSelfieCarteIdentite()); // Suppression de l'ancien selfie
                    }
                    verification.setSelfieCarteIdentite(selfiePath); // Mise à jour du chemin du selfie
                }
            }

            // Enregistrement des données mises à jour
            userVerificationService.saveVerification(verification);
            return ResponseEntity.ok("Photos sauvegardées avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'enregistrement des fichiers : " + e.getMessage());
        }
    }



    private User getAuthenticatedUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email));

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new IllegalStateException("Utilisateur non trouvé : " + email);
        }
    }

    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getVerificationDetails() {
        User user = getAuthenticatedUser(); // Récupération de l'utilisateur connecté
        UserVerification verification = userVerificationService.findOrCreateVerification(user); // Vérification ou création

        // Construction de la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());

        // Construction des URL pour les photos de profil et selfieCarteIdentite
        String photoProfilUrl = verification.getPhotoProfil() != null
                ? "http://localhost:8080/api/verification/image/" + verification.getPhotoProfil()
                : "";
        response.put("photoProfil", photoProfilUrl);

        String selfieCarteIdentiteUrl = verification.getSelfieCarteIdentite() != null
                ? "http://localhost:8080/api/verification/image/" + verification.getSelfieCarteIdentite()
                : "";
        response.put("selfieCarteIdentite", selfieCarteIdentiteUrl);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/image/{imageName}")
    public ResponseEntity<?> getImage(@PathVariable String imageName) {
        try {
            // Utilisation du service pour obtenir le fichier
            File file = fileService.getFile(imageName);
            Resource resource = new UrlResource(file.toURI());

            // Déduire le type de contenu en fonction de l'extension du fichier
            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // Si non déterminé, envoyer un type générique
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType)) // Utiliser le type MIME dynamique
                    .body(resource);
        } catch (IOException e) {
            // Retourner un message d'erreur si le fichier n'existe pas
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }





    @DeleteMapping("/delete/{photoType}")
    public ResponseEntity<String> deleteVerificationFile(@PathVariable String photoType) {
        User user = getAuthenticatedUser();
        UserVerification verification = userVerificationService.findOrCreateVerification(user);

        try {
            if ("photoProfil".equals(photoType) && verification.getPhotoProfil() != null) {
                fileService.deleteFile(verification.getPhotoProfil());
                verification.setPhotoProfil(null);  // Réinitialiser le champ photoProfil
            } else if ("selfieCarteIdentite".equals(photoType) && verification.getSelfieCarteIdentite() != null) {
                fileService.deleteFile(verification.getSelfieCarteIdentite());
                verification.setSelfieCarteIdentite(null);  // Réinitialiser le champ selfieCarteIdentite
            } else {
                return ResponseEntity.badRequest().body("Type de photo invalide ou photo inexistante.");
            }

            userVerificationService.saveVerification(verification);
            return ResponseEntity.ok("Photo supprimée avec succès !");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du fichier : " + e.getMessage());
        }
    }
}
