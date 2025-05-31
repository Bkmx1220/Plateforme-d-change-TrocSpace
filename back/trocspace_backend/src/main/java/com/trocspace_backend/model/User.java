package com.trocspace_backend.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String rawTelephone;

    @Column(nullable = false)
    private String password;

    // Relation ManyToOne pour un seul rôle par utilisateur
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference // Spécifie que cette partie sera incluse dans la sérialisation
    private UserVerification verification;


    private LocalDateTime selfieUploadDate;

    @Column(nullable = false)
    private boolean isEnabled = true; // Par défaut, le compte est activé

    @Column(nullable = false)
    private boolean isVerified = false; // Compte vérifié après validation par l'admin

    @Column(nullable = false)
    private boolean isEmailVerified = false; // Par défaut, l'email n'est pas vérifié

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Toujours actif pour cette implémentation
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Toujours non bloqué
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Toujours valide
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled; // Gestion simplifiée par isEnabled
    }
}
