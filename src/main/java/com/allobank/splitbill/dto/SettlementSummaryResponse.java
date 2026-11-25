package com.allobank.splitbill.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class SettlementSummaryResponse {

    private UUID groupId;
    private String groupName;
    private String currency;
    private BigDecimal totalExpenses;

    // Required Personalization fields
    @JsonProperty("service_charge_pct")
    private int serviceChargePct;

    @JsonProperty("service_charge_amount")
    private BigDecimal serviceChargeAmount;

    private boolean isSettled;
    private List<ParticipantBalanceDto> participantBalances;
    private List<SettlementTransactionDto> settlements;
    private List<CategorySummaryDto> categorySummaries;

    public SettlementSummaryResponse() {
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public int getServiceChargePct() {
        return serviceChargePct;
    }

    public void setServiceChargePct(int serviceChargePct) {
        this.serviceChargePct = serviceChargePct;
    }

    public BigDecimal getServiceChargeAmount() {
        return serviceChargeAmount;
    }

    public void setServiceChargeAmount(BigDecimal serviceChargeAmount) {
        this.serviceChargeAmount = serviceChargeAmount;
    }

    public boolean isSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public List<ParticipantBalanceDto> getParticipantBalances() {
        return participantBalances;
    }

    public void setParticipantBalances(List<ParticipantBalanceDto> participantBalances) {
        this.participantBalances = participantBalances;
    }

    public List<SettlementTransactionDto> getSettlements() {
        return settlements;
    }

    public void setSettlements(List<SettlementTransactionDto> settlements) {
        this.settlements = settlements;
    }

    public List<CategorySummaryDto> getCategorySummaries() {
        return categorySummaries;
    }

    public void setCategorySummaries(List<CategorySummaryDto> categorySummaries) {
        this.categorySummaries = categorySummaries;
    }
}
