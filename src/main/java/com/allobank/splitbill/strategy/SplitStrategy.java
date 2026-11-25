package com.allobank.splitbill.strategy;

import com.allobank.splitbill.dto.SplitDetailRequest;
import com.allobank.splitbill.entity.Expense;
import com.allobank.splitbill.entity.ExpenseSplit;
import com.allobank.splitbill.entity.Participant;
import com.allobank.splitbill.entity.SplitType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface SplitStrategy {
    SplitType getSplitType();

    List<ExpenseSplit> calculateSplits(
            Expense expense,
            BigDecimal totalAmount,
            List<Participant> allGroupParticipants,
            List<String> targetParticipantNames,
            List<SplitDetailRequest> explicitSplits,
            Map<String, Participant> participantLookup
    );
}
