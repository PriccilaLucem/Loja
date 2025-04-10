package org.example.loja.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.entities.RoleEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.util.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    private final UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private final String issuer = "loja";

    @Value("${PUBLIC_KEY}")
    private String publicKeyPath;

    @Value("${PRIVATE_KEY}")
    private String privateKeyPath;

    public RSAPublicKey getPublicKey() throws Exception {
        logger.debug("Loading public key from path: {}", publicKeyPath);
        try {
            String key = new String(Files.readAllBytes(Paths.get(publicKeyPath)))
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);
            logger.debug("Public key loaded successfully.");
            return publicKey;
        } catch (Exception e) {
            logger.error("Error while loading public key", e);
            throw e;
        }
    }

    public String generateUserAdminToken(StoreAdminEntity admin) throws Exception {
        logger.debug("Generating JWT for StoreAdminEntity with ID: {}", admin.getId());
        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                    (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
            );

            String token = JWT.create()
                    .withIssuer(issuer)
                    .withArrayClaim("roles", admin.getRole().stream()
                            .map(RoleEntity::getName).toArray(String[]::new))
                    .withClaim("name", admin.getName())
                    .withClaim("id", admin.getId().toString())
                    .withClaim("email", admin.getEmail())
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                    .sign(algorithm);

            logger.debug("JWT generated successfully.");
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT for StoreAdminEntity", e);
            throw e;
        }
    }
    public String getUsernameFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("email").asString();
    }

    public Authentication getAuthentication(String username) {
        logger.debug("Retrieving authentication for user: {}", username);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        logger.debug("Authentication retrieved successfully for user: {}", username);
        return authentication;
    }

    public boolean validateToken(String token) {
        logger.debug("Validating token...");
        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                    (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
            );
            JWT.require(algorithm).withIssuer(issuer).build().verify(token);
            logger.debug("Token validated successfully.");
            return true;
        } catch (Exception e) {
            logger.error("Token validation failed", e);
            return false;
        }
    }

    public String generateAdminMasterToken(AdminMasterEntity admin) throws Exception {
        logger.debug("Generating JWT for AdminMasterEntity with ID: {}", admin.getId());
        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                    (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
            );

            String token = JWT.create()
                    .withIssuer(issuer)
                    .withClaim("email", admin.getEmail())
                    .withArrayClaim("roles", admin.getRole().stream()
                            .map(RoleEntity::getName).toArray(String[]::new))
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + 4000 * 60 * 60)) // 4 horas
                    .sign(algorithm);

            logger.debug("JWT generated successfully for AdminMasterEntity.");
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT for AdminMasterEntity", e);
            throw e;
        }
    }

    public static UUID extractIdFromToken(String tokenValue) {
        logger.debug("Extracting ID from token...");
        String token = tokenValue.startsWith("Bearer ") ? tokenValue.substring(7) : tokenValue;
        if (token.isBlank() || !token.contains(".")) {
            logger.error("Invalid or missing JWT token");
            throw new IllegalArgumentException("Invalid or missing JWT token");
        }

        try {
            DecodedJWT decodedJWT = JWT.decode(token);
            String userId = decodedJWT.getClaim("id").asString();
            UUID userUuid = UUID.fromString(userId);
            logger.debug("Extracted user ID: {}", userUuid);
            return userUuid;
        } catch (JWTDecodeException e) {
            logger.error("Failed to decode JWT token", e);
            throw new IllegalArgumentException("Failed to decode JWT token", e);
        }
    }
}
