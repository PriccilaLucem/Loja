package org.example.loja.util;
import at.favre.lib.crypto.bcrypt.BCrypt;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.example.loja.entities.RoleEntity;
import org.example.loja.entities.StoreAdminEntity;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Authorization {
    public static final String TOKEN = "my-secret-long-key-token-for-generatinng-blabla";
    public static final Integer SALT = 16;

    public static PrivateKey getPrivateKey(String filename) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filename)));
        key = key.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    public static PublicKey getPublicKey(String filename) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(filename)));
        key = key.replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }


    public static boolean isAuthorized(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified;
    }

    public static String hashPassword(String password) {
        return BCrypt.withDefaults().hashToString(SALT, password.toCharArray());
    }

    public static String generateUserAdminToken(StoreAdminEntity admin) throws Exception {
        try {
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) getPublicKey("public_key.pem"), (RSAPrivateKey) getPrivateKey("private_key.pem"));

            return JWT.create()
                    .withIssuer("loja")
                    .withArrayClaim("roles", admin.getRole().stream()
                            .map(RoleEntity::getName)
                            .toArray(String[]::new))
                    .withClaim("name", admin.getName())
                    .withClaim("id", admin.getId().toString())
                    .withClaim("email", admin.getEmail())
                    .sign(algorithm);
        } catch (Exception e) {
            throw new Exception("Error generating token");
        }
    }
}
