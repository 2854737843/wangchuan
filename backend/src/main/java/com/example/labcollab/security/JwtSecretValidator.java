package com.example.labcollab.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class JwtSecretValidator {

    private static final String DEV_DEFAULT = "LabCollabJwtSecretKeyForDevOnly_ChangeMe1234567890";

    @Value("${app.jwt.secret}")
    private String secret;

    private final Environment environment;

    public JwtSecretValidator(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void validate() {
        boolean productionLike = Arrays.stream(environment.getActiveProfiles())
                .anyMatch(profile -> profile.equalsIgnoreCase("prod") || profile.equalsIgnoreCase("production"));
        if (productionLike && DEV_DEFAULT.equals(secret)) {
            throw new IllegalStateException("JWT secret must be overridden in production profile");
        }
    }
}
