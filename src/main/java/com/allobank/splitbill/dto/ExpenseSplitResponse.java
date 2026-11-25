package com.allobank.splitbill.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ExpenseSplitResponse {
    private UUID id;
    private String participantName;
    private BigDecimal amount;
    private BigDecimal percentage;

    public ExpenseSplitResponse() {
    }

    public ExpenseSplitResponse(UUID id, String participantName, BigDecimal amount, BigDecimal percentage) {
        this.id = id;
        this.participantName = participantName;
        this.amount = amount;
        this.percentage = percentage;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}
