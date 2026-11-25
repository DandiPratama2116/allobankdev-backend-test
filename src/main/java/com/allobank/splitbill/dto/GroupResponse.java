package com.allobank.splitbill.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GroupResponse {
    private UUID id;
    private String name;
    private String description;
    private String currency;
    private List<ParticipantResponse> participants;
    private int totalExpensesCount;
    private Instant createdAt;
    private Instant updatedAt;

    public GroupResponse() {
    }

    public GroupResponse(UUID id, String name, String description, String currency, List<ParticipantResponse> participants, int totalExpensesCount, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.participants = participants;
        this.totalExpensesCount = totalExpensesCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<ParticipantResponse> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantResponse> participants) {
        this.participants = participants;
    }

    public int getTotalExpensesCount() {
        return totalExpensesCount;
    }

    public void setTotalExpensesCount(int totalExpensesCount) {
        this.totalExpensesCount = totalExpensesCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
