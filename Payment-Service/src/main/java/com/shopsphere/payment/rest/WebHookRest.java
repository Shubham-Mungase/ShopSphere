package com.shopsphere.payment.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.payment.service.WebHookService;

@RestController
@RequestMapping("/payments/webhook")
public class WebHookRest {

    private final WebHookService webhookService;

    public WebHookRest(WebHookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<Void> handleWebhook(
            @RequestBody String rawPayload,
            @RequestHeader("X-Signature") String signature) {

        webhookService.processWebHook(rawPayload, signature);
        return ResponseEntity.ok().build();
    }
}
