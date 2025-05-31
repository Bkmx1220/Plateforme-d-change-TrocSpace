package com.trocspace_backend.dto;

import com.trocspace_backend.model.Role;
import com.trocspace_backend.model.UserVerification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data // Génère les getters, setters, equals, hashCode et toString
@AllArgsConstructor // Génère un constructeur avec tous les champs
@NoArgsConstructor  // Génère un constructeur sans argument
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String rawTelephone;
    private Role role; // Inclure les détails du rôle
    private UserVerification verification; // Inclure les informations de vérification
    private LocalDateTime selfieUploadDate;
    private boolean isEnabled;
    private boolean isVerified;
    private boolean isEmailVerified;
}
