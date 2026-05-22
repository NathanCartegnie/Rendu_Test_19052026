package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PriceCalculatorTest {

    private static final double EPS = 1e-6;

    @Test
    public void calculateTotalPrice_nominal() {
        PriceCalculator pc = new PriceCalculator();
        assertEquals(30.0, pc.calculateTotalPrice(10.0, 3), EPS);
    }

    @Test
    public void applyDiscount_nominal() {
        PriceCalculator pc = new PriceCalculator();
        assertEquals(80.0, pc.applyDiscount(100.0, 0.20), EPS);
    }

    @Test
    public void calculateVat_nominal() {
        PriceCalculator pc = new PriceCalculator();
        assertEquals(20.0, pc.calculateVat(100.0, 0.20), EPS);
    }

    @Test
    public void calculatePriceWithVat_nominal() {
        PriceCalculator pc = new PriceCalculator();
        assertEquals(120.0, pc.calculatePriceWithVat(100.0, 0.20), EPS);
    }

    // error cases
    @Test
    public void calculateTotalPrice_negativeUnitPrice_shouldThrow() {
        PriceCalculator pc = new PriceCalculator();
        assertThrows(IllegalArgumentException.class, () -> pc.calculateTotalPrice(-1.0, 1));
    }

    @Test
    public void calculateTotalPrice_negativeQuantity_shouldThrow() {
        PriceCalculator pc = new PriceCalculator();
        assertThrows(IllegalArgumentException.class, () -> pc.calculateTotalPrice(1.0, -5));
    }

    @Test
    public void applyDiscount_negativeRate_shouldThrow() {
        PriceCalculator pc = new PriceCalculator();
        assertThrows(IllegalArgumentException.class, () -> pc.applyDiscount(100.0, -0.1));
    }

    @Test
    public void calculateVat_negativeRate_shouldThrow() {
        PriceCalculator pc = new PriceCalculator();
        assertThrows(IllegalArgumentException.class, () -> pc.calculateVat(100.0, -0.2));
    }

    @Test
    public void calculatePriceWithVat_negativeRate_shouldThrow() {
        PriceCalculator pc = new PriceCalculator();
        assertThrows(IllegalArgumentException.class, () -> pc.calculatePriceWithVat(100.0, -0.2));
    }
}

