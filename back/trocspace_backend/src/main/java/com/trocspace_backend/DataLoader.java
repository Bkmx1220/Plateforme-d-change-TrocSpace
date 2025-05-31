package com.trocspace_backend;

import com.trocspace_backend.model.Role;
import com.trocspace_backend.model.User;
import com.trocspace_backend.repository.RoleRepository;
import com.trocspace_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // Vérifier si le rôle ADMIN existe déjà
            Role adminRole = roleRepository.findByName("ADMIN");
            if (adminRole == null) {
                adminRole = new Role();
                adminRole.setName("ADMIN");
                roleRepository.save(adminRole);
            }

            // Création de l'utilisateur admin
            User admin = new User();
            admin.setNom("Admin");
            admin.setPrenom("Admin");
            admin.setEmail("admin@trocspace.com");
            admin.setRawTelephone("99576313");
            admin.setPassword(passwordEncoder.encode("adminPassword"));
            admin.setVerified(true);
            admin.setEmailVerified(true);

            // Assignation du rôle ADMIN à l'utilisateur
            admin.setRole(adminRole);

            userRepository.save(admin);
            System.out.println("Utilisateur admin créé avec succès.");
        }
    }
}
