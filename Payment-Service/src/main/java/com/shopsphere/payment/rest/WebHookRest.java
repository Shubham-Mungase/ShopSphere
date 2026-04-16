package com.shopsphere.payment.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.shopsphere.payment.service.WebHookService;

@RestController
@RequestMapping("/api/webhook")
public class WebHookRest {

	private final WebHookService hookService;
	
	
    public WebHookRest(WebHookService hookService) {
		super();
		this.hookService = hookService;
	}


	@PostMapping("/razorpay")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {

        System.out.println("Webhook received!");
        System.out.println("Payload: " + payload);
        System.out.println("Signature: " + signature);

        hookService.processWebHook(payload, signature);
        return ResponseEntity.ok("Webhook received");
    }
}