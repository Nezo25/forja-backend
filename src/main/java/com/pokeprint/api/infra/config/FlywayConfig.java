package com.pokeprint.api.infra.config;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            try {
                flyway.repair();
                flyway.migrate();
            } catch (Exception e) {
                System.err.println("Flyway migration failed even after repair: " + e.getMessage());
                throw e;
            }
        };
    }
}
