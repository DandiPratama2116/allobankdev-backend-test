package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.PaymentResponse;
import com.allobank.splitbill.dto.RecordPaymentRequest;
import com.allobank.splitbill.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> recordPayment(
            @PathVariable UUID groupId,
            @Valid @RequestBody RecordPaymentRequest request) {
        PaymentResponse response = paymentService.recordPayment(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getPayments(@PathVariable UUID groupId) {
        return ResponseEntity.ok(paymentService.getPaymentsByGroup(groupId));
    }
}
