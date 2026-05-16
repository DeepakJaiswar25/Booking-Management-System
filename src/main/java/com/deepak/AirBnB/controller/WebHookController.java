package com.deepak.AirBnB.controller;

import com.deepak.AirBnB.service.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebHookController {

    private final BookingService bookingService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping("/payment")
    public ResponseEntity<Void> handlePaymentWebhook(@RequestBody String payload,@RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader,webhookSecret);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
    }


}
