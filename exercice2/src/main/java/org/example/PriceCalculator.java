package org.example;

public class PriceCalculator {

    public double calculateTotalPrice(double unitPrice, int quantity) {
        if (unitPrice < 0) throw new IllegalArgumentException("unitPrice must be >= 0");
        if (quantity < 0) throw new IllegalArgumentException("quantity must be >= 0");
        return unitPrice * quantity;
    }

    public double applyDiscount(double price, double discountRate) {
        if (discountRate < 0) throw new IllegalArgumentException("discountRate must be >= 0");
        return price * (1.0 - discountRate);
    }

    public double calculateVat(double price, double vatRate) {
        if (vatRate < 0) throw new IllegalArgumentException("vatRate must be >= 0");
        return price * vatRate;
    }

    public double calculatePriceWithVat(double price, double vatRate) {
        if (vatRate < 0) throw new IllegalArgumentException("vatRate must be >= 0");
        return price + calculateVat(price, vatRate);
    }
}

