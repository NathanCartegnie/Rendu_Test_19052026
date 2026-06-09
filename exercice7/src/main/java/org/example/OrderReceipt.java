package org.example;

public class OrderReceipt {
    private final String productRef;
    private final int quantity;
    private final double total;
    private final String message;

    public OrderReceipt(String productRef, int quantity, double total, String message) {
        this.productRef = productRef;
        this.quantity = quantity;
        this.total = total;
        this.message = message;
    }

    public String getProductRef() {
        return productRef;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }

    public String getMessage() {
        return message;
    }
}

