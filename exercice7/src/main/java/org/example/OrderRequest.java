package org.example;

public class OrderRequest {
    private final String email;
    private final String productRef;
    private final int quantity;

    public OrderRequest(String email, String productRef, int quantity, CustomerProfile currentProfile) {
        this.email = email;
        this.productRef = productRef;
        this.quantity = quantity;
    }

    public String getEmail() {
        return email;
    }

    public String getProductRef() {
        return productRef;
    }

    public int getQuantity() {
        return quantity;
    }
}

