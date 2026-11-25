package com.allobank.splitbill.strategy;

import com.allobank.splitbill.dto.SplitDetailRequest;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.ExpenseCategory;
import com.allobank.splitbill.entity.ExpenseSplit;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.entity.SplitType;
import com.allobank.splitbill.exception.InvalidSplitException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SplitStrategyTest {

    private EqualSplitStrategy equalStrategy;
    private ExactSplitStrategy exactStrategy;
    private PercentageSplitStrategy percentageStrategy;

    private Participant p1;
    private Participant p2;
    private Participant p3;
    private List<Participant> participants;
    private Map<String, Participant> lookup;

    @BeforeEach
    void setUp() {
        equalStrategy = new EqualSplitStrategy();
        exactStrategy = new ExactSplitStrategy();
        percentageStrategy = new PercentageSplitStrategy();

        p1 = new Participant("Alice", null);
        p2 = new Participant("Bob", null);
        p3 = new Participant("Charlie", null);

        participants = List.of(p1, p2, p3);
        lookup = new HashMap<>();
        lookup.put("alice", p1);
        lookup.put("bob", p2);
        lookup.put("charlie", p3);
    }

    @Test
    @DisplayName("Equal split should distribute indivisible cents fairly and ensure exact sum")
    void testEqualSplitIndivisibleRemainder() {
        // 100.00 divided by 3 people = 33.33 each with 0.01 cent remainder
        // Expect: 33.34, 33.33, 33.33 -> sum = 100.00 exactly
        BigDecimal total = new BigDecimal("100.00");
        Expense expense = new Expense("Test", ExpenseCategory.OTHER, total, p1, SplitType.EQUAL);

        List<ExpenseSplit> splits = equalStrategy.calculateSplits(expense, total, participants, null, null, lookup);

        assertEquals(3, splits.size());
        assertEquals(new BigDecimal("33.34"), splits.get(0).getAmount());
        assertEquals(new BigDecimal("33.33"), splits.get(1).getAmount());
        assertEquals(new BigDecimal("33.33"), splits.get(2).getAmount());

        BigDecimal sum = splits.stream().map(ExpenseSplit::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(total, sum);
    }

    @Test
    @DisplayName("Exact split should validate sum matches total amount")
    void testExactSplitValidation() {
        BigDecimal total = new BigDecimal("100.00");
        Expense expense = new Expense("Test", ExpenseCategory.OTHER, total, p1, SplitType.EXACT);

        // Valid exact split
        List<SplitDetailRequest> validDetails = List.of(
                new SplitDetailRequest("Alice", new BigDecimal("50.00"), null),
                new SplitDetailRequest("Bob", new BigDecimal("30.00"), null),
                new SplitDetailRequest("Charlie", new BigDecimal("20.00"), null)
        );

        List<ExpenseSplit> splits = exactStrategy.calculateSplits(expense, total, participants, null, validDetails, lookup);
        assertEquals(3, splits.size());

        // Invalid exact split (sum = 95.00 != 100.00)
        List<SplitDetailRequest> invalidDetails = List.of(
                new SplitDetailRequest("Alice", new BigDecimal("50.00"), null),
                new SplitDetailRequest("Bob", new BigDecimal("30.00"), null),
                new SplitDetailRequest("Charlie", new BigDecimal("15.00"), null)
        );

        assertThrows(InvalidSplitException.class, () ->
                exactStrategy.calculateSplits(expense, total, participants, null, invalidDetails, lookup));
    }

    @Test
    @DisplayName("Percentage split should enforce 100% total and distribute rounding accurately")
    void testPercentageSplit() {
        BigDecimal total = new BigDecimal("100.00");
        Expense expense = new Expense("Test", ExpenseCategory.OTHER, total, p1, SplitType.PERCENTAGE);

        List<SplitDetailRequest> details = List.of(
                new SplitDetailRequest("Alice", null, new BigDecimal("33.33")),
                new SplitDetailRequest("Bob", null, new BigDecimal("33.33")),
                new SplitDetailRequest("Charlie", null, new BigDecimal("33.34"))
        );

        List<ExpenseSplit> splits = percentageStrategy.calculateSplits(expense, total, participants, null, details, lookup);

        BigDecimal sum = splits.stream().map(ExpenseSplit::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(total, sum);
    }
}
