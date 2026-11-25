package com.allobank.splitbill.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PersonalizationServiceTest {

    private final PersonalizationService service = new PersonalizationService("dandipratama");

    @Test
    @DisplayName("Should correctly calculate service_charge_pct for johndoe47 as documented in specification")
    void testJohnDoeExample() {
        // Example in prompt: johndoe47
        // 106+111+104+110+100+111+101+52+55 = 850
        // 850 % 10 = 0
        int pct = service.calculateServiceChargePct("johndoe47");
        assertEquals(0, pct);
    }

    @Test
    @DisplayName("Should correctly calculate service_charge_pct for candidate username dandipratama")
    void testCandidateUsernameCalculation() {
        // d: 100, a: 97, n: 110, d: 100, i: 105, p: 112, r: 114, a: 97, t: 116, a: 97, m: 109, a: 97
        // Sum = 1254 -> 1254 % 10 = 4 (4%)
        int pct = service.calculateServiceChargePct("dandipratama");
        assertEquals(4, pct);
    }

    @Test
    @DisplayName("Should compute service_charge_amount with exact BigDecimal scaling")
    void testServiceChargeAmountComputation() {
        BigDecimal totalExpenses = new BigDecimal("250.00");
        int pct = 4;
        BigDecimal amount = service.calculateServiceChargeAmount(totalExpenses, pct);
        // 250.00 * 4% = 10.00
        assertEquals(new BigDecimal("10.00"), amount);
    }
}
