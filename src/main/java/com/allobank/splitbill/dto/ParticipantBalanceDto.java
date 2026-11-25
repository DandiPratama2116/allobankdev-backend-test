package com.allobank.splitbill.dto;

import java.math.BigDecimal;

public class ParticipantBalanceDto {
    private String participantName;
    private BigDecimal totalPaid;
    private BigDecimal totalShareOwed;
    private BigDecimal netBalance;
    private String status; // OWES, IS_OWED, SETTLED

    public ParticipantBalanceDto() {
    }

    public ParticipantBalanceDto(String participantName, BigDecimal totalPaid, BigDecimal totalShareOwed, BigDecimal netBalance, String status) {
        this.participantName = participantName;
        this.totalPaid = totalPaid;
        this.totalShareOwed = totalShareOwed;
        this.netBalance = netBalance;
        this.status = status;
    }

    public String getParticipantName() {
        return participantName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public BigDecimal getTotalShareOwed() {
        return totalShareOwed;
    }

    public void setTotalShareOwed(BigDecimal totalShareOwed) {
        this.totalShareOwed = totalShareOwed;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
