package com.shopsphere.payment.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class WebhookSignatureVerifier {

    private static final String HMAC_ALGO = "HmacSHA256";

    public boolean verify(String payload, String receivedSignature, String secret) {
        try {
            String generatedSignature = generateSignature(payload, secret);
            System.out.println("---- WEBHOOK DEBUG ----");
            System.out.println("Payload   : " + payload);
            System.out.println("Secret    : " + secret);
            System.out.println("Generated : " + generatedSignature);
            System.out.println("Received  : " + receivedSignature);
            System.out.println("-----------------------");
            return generatedSignature.equals(receivedSignature);
            
        } catch (Exception e) {
            return false;
        }
    }

    private String generateSignature(String payload, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {

        Mac mac = Mac.getInstance(HMAC_ALGO);
        SecretKeySpec keySpec =
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGO);
        mac.init(keySpec);

        byte[] rawMac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(rawMac);
    }

    private String bytesToHex(byte[] rawMac) {
        StringBuilder hex = new StringBuilder();
        for (byte b : rawMac) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}
