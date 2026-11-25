package com.allobank.splitbill.dto;

import com.allobank.splitbill.entity.ExpenseCategory;
import java.math.BigDecimal;

public class CategorySummaryDto {
    private ExpenseCategory category;
    private BigDecimal totalAmount;
    private int expenseCount;

    public CategorySummaryDto() {
    }

    public CategorySummaryDto(ExpenseCategory category, BigDecimal totalAmount, int expenseCount) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.expenseCount = expenseCount;
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

    public int getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(int expenseCount) {
        this.expenseCount = expenseCount;
    }
}
