package com.trocspace_backend.service;

import com.trocspace_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data  // Lombok génère tous les getters, setters, equals, hashCode et toString
@AllArgsConstructor  // Lombok génère le constructeur avec tous les paramètres
public class CustomUserDetails implements UserDetails {

	private final User user;  // Utilisation de final pour l'utilisateur, car il est un paramètre immuable

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// Retourner le rôle unique de l'utilisateur sous forme de liste
		return Collections.singletonList(() -> user.getRole().getName()); // Utilisation de singletonList pour retourner une seule autorité
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// Si vous avez besoin de la méthode getNom() spécifiquement
	public String getNom() {
		return user.getNom();
	}
}
