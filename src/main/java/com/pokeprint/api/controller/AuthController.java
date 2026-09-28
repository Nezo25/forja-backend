package com.pokeprint.api.controller;

import com.pokeprint.api.dto.request.CheckoutAuthRequestDTO;
import com.pokeprint.api.dto.response.AuthResponseDTO;
import com.pokeprint.api.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/checkout-login")
    public ResponseEntity<AuthResponseDTO> checkoutLogin(@Valid @RequestBody CheckoutAuthRequestDTO request) {
        AuthResponseDTO response = authenticationService.processCheckoutAuthentication(request);
        return ResponseEntity.ok(response);
    }
}
