package org.example.loja.config.security.dotenv;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadDotenv() {
        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String publicKey = dotenv.get("PUBLIC_KEY");
        String privateKey = dotenv.get("PRIVATE_KEY");

        System.setProperty("PUBLIC_KEY", publicKey);
        System.setProperty("PRIVATE_KEY", privateKey);

        System.out.println("PUBLIC_KEY: " + publicKey);
        System.out.println("PRIVATE_KEY: " + privateKey);
    }
}