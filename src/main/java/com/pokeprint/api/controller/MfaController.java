package com.pokeprint.api.controller;

import com.pokeprint.api.service.MfaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/mfa")
public class MfaController {
    private final MfaService mfaService;

    public MfaController(MfaService mfaService) {
        this.mfaService = mfaService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateMfaQrCode(@RequestParam String email) throws Exception {
        String secret = mfaService.generateSecret();
        // TODO: Save secret to user in DB
        String qrCodeUri = mfaService.generateQrCodeImageUri(secret, email);
        return ResponseEntity.ok(qrCodeUri);
    }

    @PostMapping("/verify")
    public ResponseEntity<Boolean> verifyMfaCode(@RequestParam String secret, @RequestParam String code) {
        boolean isValid = mfaService.verifyCode(secret, code);
        return ResponseEntity.ok(isValid);
    }
}
