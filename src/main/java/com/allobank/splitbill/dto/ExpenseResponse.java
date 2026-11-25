package com.allobank.splitbill.dto;

import com.allobank.splitbill.entity.ExpenseCategory;
import com.allobank.splitbill.entity.SplitType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ExpenseResponse {
    private UUID id;
    private String description;
    private ExpenseCategory category;
    private BigDecimal totalAmount;
    private String paidBy;
    private SplitType splitType;
    private List<ExpenseSplitResponse> splits;
    private Instant createdAt;

    public ExpenseResponse() {
    }

    public ExpenseResponse(UUID id, String description, ExpenseCategory category, BigDecimal totalAmount, String paidBy, SplitType splitType, List<ExpenseSplitResponse> splits, Instant createdAt) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.totalAmount = totalAmount;
        this.paidBy = paidBy;
        this.splitType = splitType;
        this.splits = splits;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        this.category = category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public void setPaidBy(String paidBy) {
        this.paidBy = paidBy;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public List<ExpenseSplitResponse> getSplits() {
        return splits;
    }

    public void setSplits(List<ExpenseSplitResponse> splits) {
        this.splits = splits;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
