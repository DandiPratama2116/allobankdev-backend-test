package com.allobank.splitbill.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class PaymentResponse {
    private UUID id;
    private String payer;
    private String payee;
    private BigDecimal amount;
    private String notes;
    private Instant createdAt;

    public PaymentResponse() {
    }

    public PaymentResponse(UUID id, String payer, String payee, BigDecimal amount, String notes, Instant createdAt) {
        this.id = id;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
