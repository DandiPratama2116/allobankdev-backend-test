package com.allobank.splitbill.dto;

import java.math.BigDecimal;

public class SplitDetailRequest {
    private String participantName;
    private BigDecimal amount;
    private BigDecimal percentage;

    public SplitDetailRequest() {
    }

    public SplitDetailRequest(String participantName, BigDecimal amount, BigDecimal percentage) {
        this.participantName = participantName;
        this.amount = amount;
        this.percentage = percentage;
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
