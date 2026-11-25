package com.allobank.splitbill.strategy;

import com.allobank.splitbill.entity.SplitType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class SplitStrategyFactory {

    private final Map<SplitType, SplitStrategy> strategies = new EnumMap<>(SplitType.class);

    public SplitStrategyFactory(List<SplitStrategy> strategyList) {
        for (SplitStrategy strategy : strategyList) {
            strategies.put(strategy.getSplitType(), strategy);
        }
    }

    public SplitStrategy getStrategy(SplitType splitType) {
        SplitStrategy strategy = strategies.get(splitType);
        if (strategy == null) {
            throw new IllegalArgumentException("No split strategy configured for type: " + splitType);
        }
        return strategy;
    }
}
