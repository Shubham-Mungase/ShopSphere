package com.shopsphere.payment.utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.shopsphere.payment.exception.WebhookVerificationException;

@Component
public class WebhookSignatureVerifier {

    private static final Logger log = LoggerFactory.getLogger(WebhookSignatureVerifier.class);
    private static final String HMAC_ALGO = "HmacSHA256";

    /**
     * Verify webhook signature
     *
     * @param payload            The webhook payload
     * @param receivedSignature  Signature received from payment provider
     * @param secret             Secret key
     * @return true if verified
     */
    public boolean verify(String payload, String receivedSignature, String secret) {
        try {
            String generatedSignature = generateSignature(payload, secret);
            boolean verified = generatedSignature.equals(receivedSignature);

            if (!verified) {
                log.warn("Webhook signature verification failed. Payload={}, ReceivedSignature={}", payload, receivedSignature);
                throw new WebhookVerificationException("Invalid webhook signature");
            }

            log.info("Webhook signature verified successfully");
            return true;

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error while verifying webhook signature", e);
            throw new WebhookVerificationException("Error verifying webhook signature", e);
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