package com.pokeprint.api.infra.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookSignatureValidatorService {

    @Value("${api.mercadopago.webhook-secret:REPLACE_WITH_YOUR_MP_SECRET_KEY_PROD}")
    private String webhookSecret;

    private static final long MAX_TIME_DRIFT_SECONDS = 300; // 5 minutes Replay Attack Prevention

    public boolean validateSignature(String xSignatureHeader, String xRequestIdHeader, String dataId) {
        if (xSignatureHeader == null || xSignatureHeader.isBlank() || dataId == null) {
            return false;
        }

        Map<String, String> parts = parseSignatureHeader(xSignatureHeader);
        String ts = parts.get("ts");
        String v1 = parts.get("v1");

        if (ts == null || v1 == null) {
            return false;
        }

        // Prevent Replay Attacks
        long timestamp = Long.parseLong(ts);
        long now = Instant.now().getEpochSecond();
        if (Math.abs(now - timestamp) > MAX_TIME_DRIFT_SECONDS) {
            return false; // Replay attack or massive delay detected
        }

        // HMAC-SHA256 Manifesto: id=data.id;request-id=x-request-id;ts=ts
        String manifest = "id:" + dataId + ";request-id:" + (xRequestIdHeader != null ? xRequestIdHeader : "") + ";ts:" + ts;
        
        String calculatedHash = generateHmacSha256(manifest, webhookSecret);
        return calculatedHash.equalsIgnoreCase(v1);
    }

    private String generateHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hashBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Falha ao gerar HMAC-SHA256", e);
        }
    }

    private Map<String, String> parseSignatureHeader(String header) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = header.split(",");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }
}
