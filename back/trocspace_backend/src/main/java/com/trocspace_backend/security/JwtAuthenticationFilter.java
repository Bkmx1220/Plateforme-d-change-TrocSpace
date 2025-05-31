package com.trocspace_backend.security;

import com.trocspace_backend.service.CustomUserDetailsService;
import com.trocspace_backend.service.JwtService;
import jakarta.annotation.Nullable;
import jakarta.servlet.ServletException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws IOException, ServletException {
        String authHeader = request.getHeader("Authorization");

        // Vérifie la présence du token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            assert filterChain != null;
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Supprime "Bearer "
        String username = jwtService.extractUsername(token);

        // Vérifie si le token est valide et si l'utilisateur n'est pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtService.isAuthTokenValid(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // Met à jour le contexte de sécurité
            }
        }
        assert filterChain != null;
        filterChain.doFilter(request, response);
    }
}