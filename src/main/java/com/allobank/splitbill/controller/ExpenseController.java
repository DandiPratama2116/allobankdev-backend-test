package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.CreateExpenseRequest;
import com.allobank.splitbill.dto.ExpenseResponse;
import com.allobank.splitbill.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<ExpenseResponse> addExpense(
            @PathVariable UUID groupId,
            @Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse response = expenseService.addExpense(groupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(@PathVariable UUID groupId) {
        return ResponseEntity.ok(expenseService.getExpensesByGroup(groupId));
    }
}
