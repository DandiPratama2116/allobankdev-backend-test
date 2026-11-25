package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.*;
import com.allobank.splitbill.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class SettlementCalculationService {

    private final PersonalizationService personalizationService;

    public SettlementCalculationService(PersonalizationService personalizationService) {
        this.personalizationService = personalizationService;
    }

    public SettlementSummaryResponse calculateSettlement(BillGroup group, List<Expense> expenses, List<Payment> payments) {
        SettlementSummaryResponse response = new SettlementSummaryResponse();
        response.setGroupId(group.getId());
        response.setGroupName(group.getName());
        response.setCurrency(group.getCurrency());

        // Tracking per participant: total paid, total share owed, net balance
        Map<String, BigDecimal> totalPaidMap = new LinkedHashMap<>();
        Map<String, BigDecimal> totalShareOwedMap = new LinkedHashMap<>();
        Map<String, BigDecimal> netBalanceMap = new LinkedHashMap<>();

        for (Participant p : group.getParticipants()) {
            totalPaidMap.put(p.getName(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            totalShareOwedMap.put(p.getName(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            netBalanceMap.put(p.getName(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        // Category spending summaries
        Map<ExpenseCategory, BigDecimal> categoryTotalMap = new EnumMap<>(ExpenseCategory.class);
        Map<ExpenseCategory, Integer> categoryCountMap = new EnumMap<>(ExpenseCategory.class);
        BigDecimal totalExpenses = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        // Process expenses
        for (Expense expense : expenses) {
            BigDecimal amount = expense.getTotalAmount().setScale(2, RoundingMode.HALF_UP);
            totalExpenses = totalExpenses.add(amount);

            // Category summary
            ExpenseCategory cat = expense.getCategory();
            categoryTotalMap.put(cat, categoryTotalMap.getOrDefault(cat, BigDecimal.ZERO).add(amount));
            categoryCountMap.put(cat, categoryCountMap.getOrDefault(cat, 0) + 1);

            // Payer credit
            String payerName = expense.getPaidBy().getName();
            totalPaidMap.put(payerName, totalPaidMap.getOrDefault(payerName, BigDecimal.ZERO).add(amount));
            netBalanceMap.put(payerName, netBalanceMap.getOrDefault(payerName, BigDecimal.ZERO).add(amount));

            // Split debits
            for (ExpenseSplit split : expense.getSplits()) {
                String participantName = split.getParticipant().getName();
                BigDecimal splitAmount = split.getAmount().setScale(2, RoundingMode.HALF_UP);

                totalShareOwedMap.put(participantName, totalShareOwedMap.getOrDefault(participantName, BigDecimal.ZERO).add(splitAmount));
                netBalanceMap.put(participantName, netBalanceMap.getOrDefault(participantName, BigDecimal.ZERO).subtract(splitAmount));
            }
        }

        // Process payments (debt settlement transactions already paid)
        for (Payment payment : payments) {
            String payer = payment.getPayer().getName();
            String payee = payment.getPayee().getName();
            BigDecimal amount = payment.getAmount().setScale(2, RoundingMode.HALF_UP);

            // Payer paid -> net balance increases (debt reduced)
            netBalanceMap.put(payer, netBalanceMap.getOrDefault(payer, BigDecimal.ZERO).add(amount));
            // Payee received -> net balance decreases (credit reduced)
            netBalanceMap.put(payee, netBalanceMap.getOrDefault(payee, BigDecimal.ZERO).subtract(amount));
        }

        response.setTotalExpenses(totalExpenses);

        // Personalization calculation
        int serviceChargePct = personalizationService.calculateServiceChargePct();
        BigDecimal serviceChargeAmount = personalizationService.calculateServiceChargeAmount(totalExpenses, serviceChargePct);
        response.setServiceChargePct(serviceChargePct);
        response.setServiceChargeAmount(serviceChargeAmount);

        // Build participant balance list
        List<ParticipantBalanceDto> participantBalances = new ArrayList<>();
        boolean isAllSettled = true;

        for (Participant p : group.getParticipants()) {
            String name = p.getName();
            BigDecimal net = netBalanceMap.getOrDefault(name, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            String status;
            if (net.compareTo(new BigDecimal("0.00")) > 0) {
                status = "IS_OWED";
                isAllSettled = false;
            } else if (net.compareTo(new BigDecimal("0.00")) < 0) {
                status = "OWES";
                isAllSettled = false;
            } else {
                status = "SETTLED";
            }

            participantBalances.add(new ParticipantBalanceDto(
                    name,
                    totalPaidMap.getOrDefault(name, BigDecimal.ZERO),
                    totalShareOwedMap.getOrDefault(name, BigDecimal.ZERO),
                    net,
                    status
            ));
        }
        response.setParticipantBalances(participantBalances);
        response.setSettled(isAllSettled);

        // Category summary list
        List<CategorySummaryDto> categorySummaries = new ArrayList<>();
        for (Map.Entry<ExpenseCategory, BigDecimal> entry : categoryTotalMap.entrySet()) {
            categorySummaries.add(new CategorySummaryDto(
                    entry.getKey(),
                    entry.getValue().setScale(2, RoundingMode.HALF_UP),
                    categoryCountMap.getOrDefault(entry.getKey(), 0)
            ));
        }
        response.setCategorySummaries(categorySummaries);

        // Optimized Settlement Calculation (Minimum Cash Flow algorithm)
        List<SettlementTransactionDto> settlements = calculateOptimizedSettlements(netBalanceMap);
        response.setSettlements(settlements);

        return response;
    }

    /**
     * Greedy algorithm to minimize the number of cash-flow settlement transactions.
     * Separates creditors (positive balance) and debtors (negative balance)
     * and greedily matches the largest debtor with the largest creditor.
     */
    public List<SettlementTransactionDto> calculateOptimizedSettlements(Map<String, BigDecimal> netBalances) {
        // Debtor class
        class Debtor {
            final String name;
            BigDecimal amountOwed;

            Debtor(String name, BigDecimal amountOwed) {
                this.name = name;
                this.amountOwed = amountOwed;
            }
        }

        // Creditor class
        class Creditor {
            final String name;
            BigDecimal amountCredit;

            Creditor(String name, BigDecimal amountCredit) {
                this.name = name;
                this.amountCredit = amountCredit;
            }
        }

        // Max heap for debtors (highest debt first)
        PriorityQueue<Debtor> debtorQueue = new PriorityQueue<>((a, b) -> b.amountOwed.compareTo(a.amountOwed));
        // Max heap for creditors (highest credit first)
        PriorityQueue<Creditor> creditorQueue = new PriorityQueue<>((a, b) -> b.amountCredit.compareTo(a.amountCredit));

        BigDecimal threshold = new BigDecimal("0.005");

        for (Map.Entry<String, BigDecimal> entry : netBalances.entrySet()) {
            BigDecimal balance = entry.getValue().setScale(2, RoundingMode.HALF_UP);
            if (balance.compareTo(threshold) > 0) {
                creditorQueue.add(new Creditor(entry.getKey(), balance));
            } else if (balance.compareTo(threshold.negate()) < 0) {
                debtorQueue.add(new Debtor(entry.getKey(), balance.abs()));
            }
        }

        List<SettlementTransactionDto> settlements = new ArrayList<>();

        while (!debtorQueue.isEmpty() && !creditorQueue.isEmpty()) {
            Debtor debtor = debtorQueue.poll();
            Creditor creditor = creditorQueue.poll();

            BigDecimal settlementAmount = debtor.amountOwed.min(creditor.amountCredit)
                    .setScale(2, RoundingMode.HALF_UP);

            if (settlementAmount.compareTo(new BigDecimal("0.00")) > 0) {
                settlements.add(new SettlementTransactionDto(debtor.name, creditor.name, settlementAmount));
            }

            debtor.amountOwed = debtor.amountOwed.subtract(settlementAmount);
            creditor.amountCredit = creditor.amountCredit.subtract(settlementAmount);

            if (debtor.amountOwed.compareTo(threshold) > 0) {
                debtorQueue.add(debtor);
            }
            if (creditor.amountCredit.compareTo(threshold) > 0) {
                creditorQueue.add(creditor);
            }
        }

        return settlements;
    }
}
