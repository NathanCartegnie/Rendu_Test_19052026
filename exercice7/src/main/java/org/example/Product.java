package org.example;

public class Product {
    private final String reference;
    private final String name;
    private final double unitPrice;
    private int stock;

    public Product(String reference, String name, double unitPrice, int stock) {
        this.reference = reference;
        this.name = name;
        this.unitPrice = unitPrice;
        this.stock = stock;
    }

    public String getReference() {
        return reference;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public int getStock() {
        return stock;
    }

    public void reduceStock(int amount) {
        if (amount < 0) return;
        this.stock = Math.max(0, this.stock - amount);
    }
}

