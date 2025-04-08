// config/security/JwtTokenProvider.java
package org.example.loja.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.loja.entities.AdminMasterEntity;
import org.example.loja.entities.RoleEntity;
import org.example.loja.entities.StoreAdminEntity;
import org.example.loja.util.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

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


    public String generateUserAdminToken(StoreAdminEntity admin) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(
                (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
        );

        return JWT.create()
                .withIssuer(issuer)
                .withArrayClaim("roles", admin.getRole().stream()
                        .map(RoleEntity::getName).toArray(String[]::new))
                .withClaim("name", admin.getName())
                .withClaim("id", admin.getId().toString())
                .withClaim("email", admin.getEmail())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                .sign(algorithm);
    }

    public Authentication getAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(
                    (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                    (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
            );
            JWT.require(algorithm).withIssuer(issuer).build().verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateAdminMasterToken(AdminMasterEntity admin) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(
                (RSAPublicKey) Authorization.getPublicKey(publicKeyPath),
                (RSAPrivateKey) Authorization.getPrivateKey(privateKeyPath)
        );

        return JWT.create()
                .withIssuer(issuer)
                .withClaim("email", admin.getEmail())
                .withArrayClaim("roles", admin.getRole().stream()
                        .map(RoleEntity::getName).toArray(String[]::new))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 4000 * 60 * 60))
                .sign(algorithm);
    }
    public static UUID extractIdFromToken(String token) {

        return UUID.fromString(JWT.decode(token).getClaim("id").asString());
    }
}
