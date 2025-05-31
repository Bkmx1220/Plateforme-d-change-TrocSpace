package com.trocspace_backend.service;

import com.trocspace_backend.dto.PasswordResetDTO;
import com.trocspace_backend.dto.UserLoginDTO;
import com.trocspace_backend.dto.UserRegisterDTO;
import com.trocspace_backend.model.Role;
import com.trocspace_backend.model.User;
import com.trocspace_backend.repository.UserRepository;
import com.trocspace_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Authentification de l'utilisateur
    public ResponseEntity<Object> authenticate(UserLoginDTO userLoginDTO) {
        User existingUser = userRepository.findByEmail(userLoginDTO.getEmail());

        if (existingUser == null) {
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        }

        if (!existingUser.isEmailVerified()) {
            return new ResponseEntity<>("Veuillez vérifier votre adresse email avant de vous connecter.", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(userLoginDTO.getPassword(), existingUser.getPassword())) {
            return new ResponseEntity<>("Mot de passe incorrect", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtService.generateToken(existingUser);
        return new ResponseEntity<>(Map.of("message", "Authentification réussie", "token", token), HttpStatus.OK);
    }

    // Inscription d'un nouvel utilisateur
    public ResponseEntity<Object> register(UserRegisterDTO userRegisterDTO) {
        if (!userRegisterDTO.getPassword().equals(userRegisterDTO.getPasswordConfirmation())) {
            return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas");
        }

        if (userRepository.existsByEmail(userRegisterDTO.getEmail())) {
            return new ResponseEntity<>("Email déjà utilisé", HttpStatus.BAD_REQUEST);
        }

        // Création de l'utilisateur
        User user = new User();
        user.setEmail(userRegisterDTO.getEmail());
        user.setPrenom(userRegisterDTO.getPrenom());
        user.setNom(userRegisterDTO.getNom());
        user.setRawTelephone(userRegisterDTO.getRawTelephone());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));

        // Récupérer ou créer un rôle utilisateur par défaut
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
        }

        // Assignation du rôle unique à l'utilisateur
        user.setRole(userRole);  // Remplacer la collection par un seul rôle

        userRepository.save(user);

        // Créer et envoyer un token de vérification
        String verificationToken = jwtService.generateVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);

        return new ResponseEntity<>(Map.of("token", verificationToken), HttpStatus.CREATED);
    }


    // Vérification de l'email après inscription
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(username);

        if (user == null) {
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        }

        if (jwtService.isVerificationTokenValid(token, user)) {
            user.setEmailVerified(true);
            userRepository.save(user);
            return new ResponseEntity<>("Email vérifié avec succès", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token invalide ou expiré", HttpStatus.UNAUTHORIZED);
        }
    }

    // Demande de réinitialisation de mot de passe
    public ResponseEntity<String> forgotPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("Aucun utilisateur trouvé avec cet email", HttpStatus.NOT_FOUND);
        }

        String resetToken = jwtService.generateResetPasswordToken(user);
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return new ResponseEntity<>("Email de réinitialisation envoyé", HttpStatus.OK);
    }

    // Vérification du token de réinitialisation de mot de passe
    public ResponseEntity<String> verifyResetToken(String token) {
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new ResponseEntity<>("Token invalide", HttpStatus.UNAUTHORIZED);
        }

        if (jwtService.isResetPasswordTokenValid(token, user)) {
            return new ResponseEntity<>("Token valide", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Token invalide ou expiré", HttpStatus.UNAUTHORIZED);
        }
    }

    // Réinitialisation du mot de passe
    public ResponseEntity<String> resetPassword(PasswordResetDTO passwordResetDTO) {
        String email = jwtService.extractUsername(passwordResetDTO.getToken());
        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>("Utilisateur non trouvé", HttpStatus.NOT_FOUND);
        }

        if (!jwtService.isResetPasswordTokenValid(passwordResetDTO.getToken(), user)) {
            return new ResponseEntity<>("Token invalide ou expiré", HttpStatus.UNAUTHORIZED);
        }

        // Vérifiez si le nouveau mot de passe est identique à l'ancien mot de passe
        if (passwordEncoder.matches(passwordResetDTO.getNewPassword(), user.getPassword())) {
            return new ResponseEntity<>("Le nouveau mot de passe ne peut pas être identique à l'ancien", HttpStatus.BAD_REQUEST);
        }

        // Encodez et mettez à jour le mot de passe
        user.setPassword(passwordEncoder.encode(passwordResetDTO.getNewPassword()));
        userRepository.save(user);

        return new ResponseEntity<>("Mot de passe réinitialisé avec succès", HttpStatus.OK);
    }

}
