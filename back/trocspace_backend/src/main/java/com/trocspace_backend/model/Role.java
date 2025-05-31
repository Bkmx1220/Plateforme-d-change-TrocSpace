package com.trocspace_backend.model;

import jakarta.persistence.*;
import lombok.Data; // Utilisation de @Data qui génère les getters, setters, equals, hashCode, et toString
import org.springframework.security.core.GrantedAuthority;

@Entity
@Data
@Table(name = "roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
}
