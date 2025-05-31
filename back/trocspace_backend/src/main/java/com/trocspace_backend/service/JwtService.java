    package com.trocspace_backend.service;

    import com.trocspace_backend.model.User;
    import io.jsonwebtoken.*;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;

    import javax.crypto.spec.SecretKeySpec;
    import java.util.Date;
    import java.util.function.Function;

    @Service
    public class JwtService {

        @Value("${jwt.secretKey}")
        private String secretKey;

        private static final long TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 heures
        private static final long VERIFICATION_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 heures
        private static final long RESET_PASSWORD_TOKEN_EXPIRATION_TIME = 1000 * 60 * 30; // 30 minutes

        // Récupérer le subject (par ex: email ou username) à partir du token
        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        // Extraire un claim générique du token
        public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
        }

        // Extraire tous les claims d'un token
        public Claims extractAllClaims(String token) {
            try {
                SecretKeySpec keySpec = createSigningKey();
                JwtParser parser = Jwts.parserBuilder()
                        .setSigningKey(keySpec)
                        .build();
                return parser.parseClaimsJws(token).getBody();
            } catch (JwtException e) {
                // Gérer les erreurs JWT ici
                System.err.println("Erreur lors de l'extraction des claims: " + e.getMessage());
                throw e;
            }
        }

        // Générer un token générique
        private String createToken(String subject, long expirationTime) {
            SecretKeySpec keySpec = createSigningKey();
            return Jwts.builder()
                    .setSubject(subject)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(keySpec)
                    .compact();
        }

        // Générer un token JWT pour l'authentification
        public String generateToken(UserDetails userDetails) {
            return createToken(userDetails.getUsername(), TOKEN_EXPIRATION_TIME);
        }

        // Générer un token de vérification d'email
        public String generateVerificationToken(UserDetails userDetails) {
            return createToken(userDetails.getUsername(), VERIFICATION_TOKEN_EXPIRATION_TIME);
        }

        // Générer un token de réinitialisation de mot de passe
        public String generateResetPasswordToken(UserDetails userDetails) {
            String token = createToken(userDetails.getUsername(), RESET_PASSWORD_TOKEN_EXPIRATION_TIME);
            System.out.println("Generated Reset Password Token: " + token);  // Log du token généré
            return token;
        }


        // Valider un token générique
        public boolean isTokenValid(String token, String subject) {
            try {
                Claims claims = extractAllClaims(token);
                String tokenSubject = claims.getSubject();
                return subject.equals(tokenSubject) && !isTokenExpired(claims.getExpiration());
            } catch (Exception e) {
                System.err.println("Erreur lors de la validation du token: " + e.getMessage());
                return false;
            }
        }

        // Valider un token JWT d'authentification
        public boolean isAuthTokenValid(String token, UserDetails userDetails) {
            return isTokenValid(token, userDetails.getUsername());
        }

        // Valider un token de réinitialisation de mot de passe
        public boolean isResetPasswordTokenValid(String token, UserDetails userDetails) {
            return isTokenValid(token, userDetails.getUsername()); // Utilisation de l'email de l'utilisateur via UserDetails
        }

        // Valider un token de vérification d'email
        public boolean isVerificationTokenValid(String token, UserDetails userDetails) {
            return isTokenValid(token, userDetails.getUsername()); // Utilisation de l'email de l'utilisateur via UserDetails
        }



        // Vérifier si un token est expiré
        private boolean isTokenExpired(Date expirationDate) {
            return expirationDate.before(new Date());
        }

        // Créer une clé de signature à partir du secret
        private SecretKeySpec createSigningKey() {
            return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        }

        // Extraire la date d'expiration du token
        private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
        }
    }
