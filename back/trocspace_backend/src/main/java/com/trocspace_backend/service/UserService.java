package com.trocspace_backend.service;

import com.trocspace_backend.model.User;
import com.trocspace_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Récupérer l'utilisateur connecté avec ses rôles
    public User getCurrentUserWithRoles(String email) {
        return userRepository.findByEmail(email);
    }

    // Récupérer un utilisateur par son ID
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    // Méthode pour récupérer un utilisateur par son email
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userRepository.findByEmail(username)); // Supposons que 'email' est un champ unique dans la base de données
    }
    // Sauvegarder un utilisateur
    public void save(User user) {
        userRepository.save(user);
    }

    // Supprimer un utilisateur par son ID
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    // Récupérer la liste de tous les utilisateurs
    public List<User> findAllUsers() {
        System.out.println("Appel à userRepository.findAll()");
        List<User> users = userRepository.findAll();
        System.out.println("Nombre d'utilisateurs récupérés : " + users.size());
        return users;
    }


    // Activer ou désactiver un utilisateur
    public void toggleUserStatus(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
        }
    }

    // Marquer un utilisateur comme vérifié
    public void verifyUser(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setVerified(true); // Compte vérifié par l'admin
            user.setEmailVerified(true); // Optionnel : email vérifié aussi
            userRepository.save(user);
        }
    }
}
