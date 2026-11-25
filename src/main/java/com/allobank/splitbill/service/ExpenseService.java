package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.CreateExpenseRequest;
import com.allobank.splitbill.dto.ExpenseResponse;
import com.allobank.splitbill.dto.ExpenseSplitResponse;
import com.allobank.splitbill.entity.*;
import com.allobank.splitbill.exception.InvalidOperationException;
import com.allobank.splitbill.exception.ResourceNotFoundException;
import com.allobank.splitbill.repository.ExpenseRepository;
import com.allobank.splitbill.strategy.SplitStrategy;
import com.allobank.splitbill.strategy.SplitStrategyFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExpenseService {

    private final BillGroupService groupService;
    private final ExpenseRepository expenseRepository;
    private final SplitStrategyFactory strategyFactory;

    public ExpenseService(BillGroupService groupService, ExpenseRepository expenseRepository, SplitStrategyFactory strategyFactory) {
        this.groupService = groupService;
        this.expenseRepository = expenseRepository;
        this.strategyFactory = strategyFactory;
    }

    public ExpenseResponse addExpense(UUID groupId, CreateExpenseRequest request) {
        BillGroup group = groupService.getGroupEntity(groupId);

        // Build lookup of participant names (lowercase -> Participant)
        Map<String, Participant> participantLookup = new HashMap<>();
        for (Participant p : group.getParticipants()) {
            participantLookup.put(p.getName().toLowerCase(), p);
        }

        // Find payer
        Participant payer = participantLookup.get(request.getPaidBy().trim().toLowerCase());
        if (payer == null) {
            throw new InvalidOperationException("Payer '" + request.getPaidBy() + "' is not a participant in group: " + group.getName());
        }

        BigDecimal amount = request.getAmount().setScale(2, RoundingMode.HALF_UP);
        SplitType splitType = request.getSplitType() != null ? request.getSplitType() : SplitType.EQUAL;

        Expense expense = new Expense(
                request.getDescription(),
                request.getCategory(),
                amount,
                payer,
                splitType
        );

        // Calculate splits via strategy
        SplitStrategy strategy = strategyFactory.getStrategy(splitType);
        List<ExpenseSplit> splits = strategy.calculateSplits(
                expense,
                amount,
                group.getParticipants(),
                request.getSplitAmong(),
                request.getSplits(),
                participantLookup
        );

        for (ExpenseSplit split : splits) {
            expense.addSplit(split);
        }

        group.addExpense(expense);
        Expense saved = expenseRepository.save(expense);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByGroup(UUID groupId) {
        groupService.getGroupById(groupId); // Validate existence
        return expenseRepository.findByBillGroupIdWithDetails(groupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        List<ExpenseSplitResponse> splitResponses = expense.getSplits().stream()
                .map(s -> new ExpenseSplitResponse(s.getId(), s.getParticipant().getName(), s.getAmount(), s.getPercentage()))
                .collect(Collectors.toList());

        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getCategory(),
                expense.getTotalAmount(),
                expense.getPaidBy().getName(),
                expense.getSplitType(),
                splitResponses,
                expense.getCreatedAt()
        );
    }
}
