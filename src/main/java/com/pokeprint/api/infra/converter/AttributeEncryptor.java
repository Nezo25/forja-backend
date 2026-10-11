package com.pokeprint.api.infra.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Converter
public class AttributeEncryptor implements AttributeConverter<String, String> {
    private static final String AES = "AES";
    
    @Value("${db.encryption.key:12345678901234567890123456789012}")
    private String encryptionKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptionKey.getBytes(), AES));
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (Exception e) {
            throw new IllegalStateException("Error encrypting attribute", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance(AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(encryptionKey.getBytes(), AES));
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (Exception e) {
            throw new IllegalStateException("Error decrypting attribute", e);
        }
    }
}
