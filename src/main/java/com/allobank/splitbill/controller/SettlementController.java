package com.allobank.splitbill.controller;

import com.allobank.splitbill.dto.SettlementSummaryResponse;
import com.allobank.splitbill.entity.BillGroup;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.Payment;
import com.allobank.splitbill.repository.ExpenseRepository;
import com.allobank.splitbill.repository.PaymentRepository;
import com.allobank.splitbill.service.BillGroupService;
import com.allobank.splitbill.service.SettlementCalculationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups/{groupId}/settlement")
public class SettlementController {

    private final BillGroupService groupService;
    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final SettlementCalculationService settlementService;

    public SettlementController(
            BillGroupService groupService,
            ExpenseRepository expenseRepository,
            PaymentRepository paymentRepository,
            SettlementCalculationService settlementService) {
        this.groupService = groupService;
        this.expenseRepository = expenseRepository;
        this.paymentRepository = paymentRepository;
        this.settlementService = settlementService;
    }

    @GetMapping
    public ResponseEntity<SettlementSummaryResponse> getSettlementSummary(@PathVariable UUID groupId) {
        BillGroup group = groupService.getGroupEntity(groupId);
        List<Expense> expenses = expenseRepository.findByBillGroupIdWithDetails(groupId);
        List<Payment> payments = paymentRepository.findByBillGroupIdWithDetails(groupId);

        SettlementSummaryResponse summary = settlementService.calculateSettlement(group, expenses, payments);
        return ResponseEntity.ok(summary);
    }
}
