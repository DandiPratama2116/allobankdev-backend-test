package com.allobank.splitbill.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Service to compute required personalization fields:
 * - service_charge_pct = (sum of ASCII/Unicode values of lowercase GitHub username) % 10
 * - service_charge_amount = (totalExpenses * service_charge_pct) / 100
 *
 * This value is dynamically computed in code as strictly required by the challenge.
 */
@Service
public class PersonalizationService {

    private final String githubUsername;

    public PersonalizationService(@Value("${app.personalization.github-username:dandipratama}") String githubUsername) {
        this.githubUsername = githubUsername != null ? githubUsername.trim() : "dandipratama";
    }

    public String getGithubUsername() {
        return githubUsername;
    }

    /**
     * Calculates the service charge percentage based on GitHub username:
     * 1. Take GitHub username in lowercase
     * 2. Sum the Unicode (ASCII) values of all characters
     * 3. service_charge_pct = (sum % 10)
     */
    public int calculateServiceChargePct() {
        return calculateServiceChargePct(this.githubUsername);
    }

    public int calculateServiceChargePct(String username) {
        if (username == null || username.isBlank()) {
            return 0;
        }
        String lower = username.toLowerCase();
        int sum = 0;
        for (int i = 0; i < lower.length(); i++) {
            sum += (int) lower.charAt(i);
        }
        return sum % 10;
    }

    /**
     * Calculates service charge amount:
     * totalExpenses * (service_charge_pct / 100) using BigDecimal with scale 2.
     */
    public BigDecimal calculateServiceChargeAmount(BigDecimal totalExpenses, int serviceChargePct) {
        if (totalExpenses == null || totalExpenses.compareTo(BigDecimal.ZERO) <= 0 || serviceChargePct <= 0) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        return totalExpenses.multiply(BigDecimal.valueOf(serviceChargePct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
