package com.allobank.splitbill.dto;

import com.allobank.splitbill.entity.ExpenseCategory;
import com.allobank.splitbill.entity.SplitType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public class CreateExpenseRequest {

    @NotBlank(message = "Description is required")
    private String description;

    private ExpenseCategory category = ExpenseCategory.OTHER;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Payer name is required")
    private String paidBy;

    private SplitType splitType = SplitType.EQUAL;

    // For EQUAL split, optional list of participant names to split between (defaults to all group members if omitted)
    private List<String> splitAmong;

    // For EXACT or PERCENTAGE splits
    private List<SplitDetailRequest> splits;

    public CreateExpenseRequest() {
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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public List<String> getSplitAmong() {
        return splitAmong;
    }

    public void setSplitAmong(List<String> splitAmong) {
        this.splitAmong = splitAmong;
    }

    public List<SplitDetailRequest> getSplits() {
        return splits;
    }

    public void setSplits(List<SplitDetailRequest> splits) {
        this.splits = splits;
    }
}
