package com.allobank.splitbill.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    private String currency = "IDR";

    @NotEmpty(message = "Group must have at least one participant")
    private List<String> participants;

    public CreateGroupRequest() {
    }

    public CreateGroupRequest(String name, String description, String currency, List<String> participants) {
        this.name = name;
        this.description = description;
        this.currency = currency;
        this.participants = participants;
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

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
