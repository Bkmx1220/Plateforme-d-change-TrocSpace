package com.trocspace_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    private static final String SENDER_EMAIL = "TroSpace <noreply@trospace.com>";

    // Méthode générique pour envoyer un email
    private void sendEmail(String recipientEmail, String subject, String body) {
        try {
            // Construire l'email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom(SENDER_EMAIL);

            // Envoyer l'email
            mailSender.send(message);
        } catch (Exception e) {
            // Ajouter un mécanisme de journalisation ou de gestion des erreurs
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }

    // Méthode pour envoyer un email de vérification
    public void sendVerificationEmail(String recipientEmail, String token) {
        String verificationLink = "http://localhost:5173/verify-email?token=" + token;
        String body = "Bonjour,\n\nVeuillez vérifier votre email en cliquant sur ce lien :\n" + verificationLink;
        sendEmail(recipientEmail, "Vérification de l'email", body);
    }

    // Méthode pour envoyer un email de réinitialisation de mot de passe
    public void sendPasswordResetEmail(String recipientEmail, String resetLink) {
        String body = "Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" + resetLink;
        sendEmail(recipientEmail, "Réinitialisation de votre mot de passe", body);
    }
}
