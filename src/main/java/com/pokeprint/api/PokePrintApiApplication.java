package com.pokeprint.api;

import com.pokeprint.api.domain.entity.Customer;
import com.pokeprint.api.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
public class PokePrintApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokePrintApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedAdmin(CustomerRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            Optional<Customer> adminOpt = repository.findByEmail("admin@forja.com");
            if (adminOpt.isPresent()) {
                Customer admin = adminOpt.get();
                admin.setPassword(passwordEncoder.encode("admin123"));
                repository.save(admin);
            }
        };
    }
}
