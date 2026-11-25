package com.allobank.splitbill.service;

import com.allobank.splitbill.dto.SettlementSummaryResponse;
import com.allobank.splitbill.dto.SettlementTransactionDto;
import com.allobank.splitbill.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SettlementCalculationServiceTest {

    private SettlementCalculationService settlementService;

    @BeforeEach
    void setUp() {
        PersonalizationService personalizationService = new PersonalizationService("dandipratama");
        settlementService = new SettlementCalculationService(personalizationService);
    }

    @Test
    @DisplayName("Should correctly calculate net balances and optimized settlement transactions")
    void testStandardSettlementFlow() {
        BillGroup group = new BillGroup("Bali Trip", "Vacation", "IDR");
        Participant alice = new Participant("Alice", null);
        Participant bob = new Participant("Bob", null);
        Participant charlie = new Participant("Charlie", null);

        group.addParticipant(alice);
        group.addParticipant(bob);
        group.addParticipant(charlie);

        // Expense 1: Alice pays 90.00 for Dinner, split equally (30.00 each)
        Expense expense1 = new Expense("Dinner", ExpenseCategory.FOOD_AND_DRINK, new BigDecimal("90.00"), alice, SplitType.EQUAL);
        expense1.addSplit(new ExpenseSplit(alice, new BigDecimal("30.00"), null));
        expense1.addSplit(new ExpenseSplit(bob, new BigDecimal("30.00"), null));
        expense1.addSplit(new ExpenseSplit(charlie, new BigDecimal("30.00"), null));

        // Expense 2: Bob pays 60.00 for Taxi, split equally (20.00 each)
        Expense expense2 = new Expense("Taxi", ExpenseCategory.TRANSPORTATION, new BigDecimal("60.00"), bob, SplitType.EQUAL);
        expense2.addSplit(new ExpenseSplit(alice, new BigDecimal("20.00"), null));
        expense2.addSplit(new ExpenseSplit(bob, new BigDecimal("20.00"), null));
        expense2.addSplit(new ExpenseSplit(charlie, new BigDecimal("20.00"), null));

        SettlementSummaryResponse response = settlementService.calculateSettlement(
                group,
                List.of(expense1, expense2),
                Collections.emptyList()
        );

        // Total expenses = 90.00 + 60.00 = 150.00
        assertEquals(new BigDecimal("150.00"), response.getTotalExpenses());
        assertEquals(4, response.getServiceChargePct());
        // 150.00 * 4% = 6.00
        assertEquals(new BigDecimal("6.00"), response.getServiceChargeAmount());

        // Net Balances:
        // Alice: paid 90, owes 50 -> net = +40.00
        // Bob: paid 60, owes 50 -> net = +10.00
        // Charlie: paid 0, owes 50 -> net = -50.00
        assertEquals(3, response.getParticipantBalances().size());

        // Settlement transactions check:
        // Charlie owes 50.00 total: owes 40.00 to Alice, and 10.00 to Bob
        List<SettlementTransactionDto> settlements = response.getSettlements();
        assertEquals(2, settlements.size());

        BigDecimal totalSettled = settlements.stream()
                .map(SettlementTransactionDto::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(new BigDecimal("50.00"), totalSettled);

        // Check category summaries
        assertEquals(2, response.getCategorySummaries().size());
    }

    @Test
    @DisplayName("Should update balances after a debt settlement payment is recorded")
    void testSettlementWithPayments() {
        BillGroup group = new BillGroup("Roommates", "Apartment expenses", "IDR");
        Participant alice = new Participant("Alice", null);
        Participant bob = new Participant("Bob", null);
        group.addParticipant(alice);
        group.addParticipant(bob);

        // Alice pays 100 for groceries, shared with Bob (50 each)
        Expense expense = new Expense("Groceries", ExpenseCategory.FOOD_AND_DRINK, new BigDecimal("100.00"), alice, SplitType.EQUAL);
        expense.addSplit(new ExpenseSplit(alice, new BigDecimal("50.00"), null));
        expense.addSplit(new ExpenseSplit(bob, new BigDecimal("50.00"), null));

        // Bob pays Alice 30.00 to partially settle
        Payment payment = new Payment(bob, alice, new BigDecimal("30.00"), "Partial payment");

        SettlementSummaryResponse response = settlementService.calculateSettlement(
                group,
                List.of(expense),
                List.of(payment)
        );

        // Remaining: Bob owes Alice 20.00
        List<SettlementTransactionDto> settlements = response.getSettlements();
        assertEquals(1, settlements.size());
        assertEquals("Bob", settlements.get(0).getFrom());
        assertEquals("Alice", settlements.get(0).getTo());
        assertEquals(new BigDecimal("20.00"), settlements.get(0).getAmount());
    }

    @Test
    @DisplayName("Should achieve zero transactions when all balances are fully settled")
    void testFullySettledGroup() {
        BillGroup group = new BillGroup("Lunch", null, "IDR");
        Participant alice = new Participant("Alice", null);
        Participant bob = new Participant("Bob", null);
        group.addParticipant(alice);
        group.addParticipant(bob);

        Expense expense = new Expense("Lunch", ExpenseCategory.FOOD_AND_DRINK, new BigDecimal("40.00"), alice, SplitType.EQUAL);
        expense.addSplit(new ExpenseSplit(alice, new BigDecimal("20.00"), null));
        expense.addSplit(new ExpenseSplit(bob, new BigDecimal("20.00"), null));

        Payment payment = new Payment(bob, alice, new BigDecimal("20.00"), "Full payment");

        SettlementSummaryResponse response = settlementService.calculateSettlement(
                group,
                List.of(expense),
                List.of(payment)
        );

        assertTrue(response.isSettled());
        assertTrue(response.getSettlements().isEmpty());
    }
}
