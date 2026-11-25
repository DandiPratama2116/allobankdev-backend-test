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
public class PercentageSplitStrategy implements SplitStrategy {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100.00");

    @Override
    public SplitType getSplitType() {
        return SplitType.PERCENTAGE;
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
            throw new InvalidSplitException("Splits detail is required for PERCENTAGE split type");
        }

        BigDecimal totalPercentage = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (SplitDetailRequest detail : explicitSplits) {
            if (detail.getParticipantName() == null || detail.getParticipantName().isBlank()) {
                throw new InvalidSplitException("Participant name is required in split detail");
            }
            if (detail.getPercentage() == null || detail.getPercentage().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidSplitException("Percentage for participant '" + detail.getParticipantName() + "' must be greater than 0");
            }

            Participant p = participantLookup.get(detail.getParticipantName().trim().toLowerCase());
            if (p == null) {
                throw new InvalidSplitException("Participant '" + detail.getParticipantName() + "' is not a member of this group");
            }

            totalPercentage = totalPercentage.add(detail.getPercentage().setScale(2, RoundingMode.HALF_UP));
        }

        if (totalPercentage.compareTo(ONE_HUNDRED) != 0) {
            throw new InvalidSplitException(
                    String.format("Sum of split percentages (%s%%) must equal 100.00%%", totalPercentage)
            );
        }

        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal allocatedSum = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        int maxIndex = 0;
        BigDecimal maxPct = BigDecimal.ZERO;

        for (int i = 0; i < explicitSplits.size(); i++) {
            SplitDetailRequest detail = explicitSplits.get(i);
            Participant p = participantLookup.get(detail.getParticipantName().trim().toLowerCase());
            BigDecimal pct = detail.getPercentage().setScale(2, RoundingMode.HALF_UP);

            // Compute share: (totalAmount * percentage) / 100 with floor to 2 decimal places
            BigDecimal share = totalAmount.multiply(pct)
                    .divide(ONE_HUNDRED, 2, RoundingMode.DOWN);

            allocatedSum = allocatedSum.add(share);

            if (pct.compareTo(maxPct) > 0) {
                maxPct = pct;
                maxIndex = i;
            }

            splits.add(new ExpenseSplit(p, share, pct));
        }

        // Remainder cents allocated to participant with highest percentage share to guarantee sum == totalAmount
        BigDecimal remainder = totalAmount.subtract(allocatedSum);
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            ExpenseSplit targetSplit = splits.get(maxIndex);
            targetSplit.setAmount(targetSplit.getAmount().add(remainder));
        }

        return splits;
    }
}
