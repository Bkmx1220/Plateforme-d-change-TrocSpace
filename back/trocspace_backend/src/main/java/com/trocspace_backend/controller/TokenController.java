package com.trocspace_backend.controller;

import com.trocspace_backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/decode-token")
    public String decodeToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Supprimer le "Bearer "
            return jwtService.extractAllClaims(token).toString(); // Retourne tout le contenu du token
        }
        return "Token manquant ou mal formé";
    }
}
