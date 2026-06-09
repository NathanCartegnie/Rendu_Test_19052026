package org.example;

public class OrderResponse {
    private final boolean accepted;
    private final OrderReceipt receipt;
    private final String message;

    private OrderResponse(boolean accepted, OrderReceipt receipt, String message) {
        this.accepted = accepted;
        this.receipt = receipt;
        this.message = message;
    }

    public static OrderResponse accepted(OrderReceipt receipt) {
        return new OrderResponse(true, receipt, receipt.getMessage());
    }

    public static OrderResponse refused(String message) {
        return new OrderResponse(false, null, message);
    }

    public boolean isAccepted() {
        return accepted;
    }

    public OrderReceipt getReceipt() {
        return receipt;
    }

    public String getMessage() {
        return message;
    }
}

