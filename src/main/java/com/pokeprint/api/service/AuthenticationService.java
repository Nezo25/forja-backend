package com.pokeprint.api.service;

import com.pokeprint.api.domain.entity.Customer;
import com.pokeprint.api.dto.request.CheckoutAuthRequestDTO;
import com.pokeprint.api.dto.response.AuthResponseDTO;
import com.pokeprint.api.infra.security.JwtService;
import com.pokeprint.api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponseDTO processCheckoutAuthentication(CheckoutAuthRequestDTO request) {
        Customer customer = customerRepository.findByEmail(request.email()).orElse(null);

        if (customer == null) {
            customer = new Customer();
            customer.setEmail(request.email());
            customer.setName(request.fullName());
            customer.setPhone(request.phone());
            customer.setCpf(request.cpf());
            customer.setRole("ROLE_CLIENT");

            String rawPassword = (request.password() != null && !request.password().isBlank())
                    ? request.password()
                    : UUID.randomUUID().toString();
            customer.setPassword(passwordEncoder.encode(rawPassword));
            customer = customerRepository.save(customer);
        } else {
            if (request.password() == null || request.password().isBlank()) {
                throw new BadCredentialsException("Senha e obrigatoria para clientes ja cadastrados.");
            }
            if (!passwordEncoder.matches(request.password(), customer.getPassword())) {
                throw new BadCredentialsException("Credenciais invalidas.");
            }
        }

        String token = jwtService.generateToken(customer);
        return new AuthResponseDTO(token, "Bearer", customer.getId(), customer.getRole(), customer.getEmail(), customer.getName());
    }

    public AuthResponseDTO adminLogin(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciais invalidas."));

        if (!"ROLE_ADMIN".equals(customer.getRole())) {
            throw new BadCredentialsException("Acesso nao autorizado.");
        }

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            throw new BadCredentialsException("Credenciais invalidas.");
        }

        String token = jwtService.generateToken(customer);
        return new AuthResponseDTO(token, "Bearer", customer.getId(), customer.getRole(), customer.getEmail(), customer.getName());
    }
}
