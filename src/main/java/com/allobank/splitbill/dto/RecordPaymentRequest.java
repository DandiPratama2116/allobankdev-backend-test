package com.allobank.splitbill.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RecordPaymentRequest {

    @NotBlank(message = "Payer name is required")
    private String payer;

    @NotBlank(message = "Payee name is required")
    private String payee;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private String notes;

    public RecordPaymentRequest() {
    }

    public RecordPaymentRequest(String payer, String payee, BigDecimal amount, String notes) {
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.notes = notes;
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
}
