package com.trocspace_backend.security;

import com.trocspace_backend.model.User;
import com.trocspace_backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserActivationFilter extends OncePerRequestFilter {

    private final UserService userService;

    public UserActivationFilter(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            // Extraire l'email (qui est le 'username' dans ce cas)
            String username = userDetails.getUsername();

            // Récupérer l'utilisateur par son email
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            // Vérifier si l'utilisateur est activé
            if (!user.isEnabled()) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Votre compte est désactivé. Veuillez contacter l'administrateur.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
