package com.allobank.splitbill.strategy;

import com.allobank.splitbill.dto.SplitDetailRequest;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.ExpenseSplit;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.entity.SplitType;
import com.allobank.splitbill.exception.InvalidSplitException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ExactSplitStrategy implements SplitStrategy {

    @Override
    public SplitType getSplitType() {
        return SplitType.EXACT;
    }

    @Override
    public List<ExpenseSplit> calculateSplits(
            Expense expense,
            BigDecimal totalAmount,
            List<Participant> allGroupParticipants,
            List<String> targetParticipantNames,
            List<SplitDetailRequest> explicitSplits,
            Map<String, Participant> participantLookup
    ) {
        if (explicitSplits == null || explicitSplits.isEmpty()) {
            throw new InvalidSplitException("Splits detail is required for EXACT split type");
        }

        BigDecimal sum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        List<ExpenseSplit> splits = new ArrayList<>();

        for (SplitDetailRequest detail : explicitSplits) {
            if (detail.getParticipantName() == null || detail.getParticipantName().isBlank()) {
                throw new InvalidSplitException("Participant name is required in split detail");
            }
            if (detail.getAmount() == null || detail.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidSplitException("Split amount for participant '" + detail.getParticipantName() + "' must be greater than 0");
            }

            Participant p = participantLookup.get(detail.getParticipantName().trim().toLowerCase());
            if (p == null) {
                throw new InvalidSplitException("Participant '" + detail.getParticipantName() + "' is not a member of this group");
            }

            BigDecimal amount = detail.getAmount().setScale(2, RoundingMode.HALF_UP);
            sum = sum.add(amount);

            splits.add(new ExpenseSplit(p, amount, null));
        }

        if (sum.compareTo(totalAmount.setScale(2, RoundingMode.HALF_UP)) != 0) {
            throw new InvalidSplitException(
                    String.format("Sum of exact splits (%s) does not match total expense amount (%s)", sum, totalAmount)
            );
        }

        return splits;
    }
}
