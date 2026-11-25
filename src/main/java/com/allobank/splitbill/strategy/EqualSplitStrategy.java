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
public class EqualSplitStrategy implements SplitStrategy {

    @Override
    public SplitType getSplitType() {
        return SplitType.EQUAL;
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
        List<Participant> splitParticipants = new ArrayList<>();

        if (targetParticipantNames != null && !targetParticipantNames.isEmpty()) {
            for (String name : targetParticipantNames) {
                Participant p = participantLookup.get(name.trim().toLowerCase());
                if (p == null) {
                    throw new InvalidSplitException("Participant '" + name + "' is not a member of this group");
                }
                splitParticipants.add(p);
            }
        } else {
            splitParticipants.addAll(allGroupParticipants);
        }

        if (splitParticipants.isEmpty()) {
            throw new InvalidSplitException("Cannot split expense among zero participants");
        }

        int count = splitParticipants.size();
        BigDecimal numParticipants = BigDecimal.valueOf(count);

        // Divide total amount by count with floor/down rounding to 2 decimal places
        BigDecimal baseShare = totalAmount.divide(numParticipants, 2, RoundingMode.DOWN);

        // Calculate remainder cents: remainder = totalAmount - (baseShare * count)
        BigDecimal totalBase = baseShare.multiply(numParticipants);
        BigDecimal remainder = totalAmount.subtract(totalBase);
        int remainderCents = remainder.movePointRight(2).intValue(); // e.g., 0.02 -> 2 cents

        List<ExpenseSplit> splits = new ArrayList<>();
        BigDecimal oneCent = new BigDecimal("0.01");

        for (int i = 0; i < count; i++) {
            Participant p = splitParticipants.get(i);
            BigDecimal share = baseShare;
            if (i < remainderCents) {
                share = share.add(oneCent);
            }
            ExpenseSplit split = new ExpenseSplit(p, share, null);
            splits.add(split);
        }

        return splits;
    }
}
