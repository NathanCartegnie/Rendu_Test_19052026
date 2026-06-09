package org.example;

import java.util.Optional;

public class OrderService {
    private final ProductRepository repository;

    public OrderService(ProductRepository repository) {
        this.repository = repository;
    }

    public OrderResponse processOrder(OrderRequest request, CustomerProfile profile) {
        Optional<Product> maybe = repository.findByReference(request.getProductRef());
        if (maybe.isEmpty()) {
            return OrderResponse.refused("Produit inconnu");
        }

        Product p = maybe.get();
        if (request.getQuantity() > p.getStock()) {
            return OrderResponse.refused("Stock insuffisant");
        }

        double totalBefore = p.getUnitPrice() * request.getQuantity();
        double discount = profile.getDiscountRate();
        double total = totalBefore * (1.0 - discount);

        p.reduceStock(request.getQuantity());

        OrderReceipt receipt = new OrderReceipt(p.getReference(), request.getQuantity(), total, "Commande acceptée");
        return OrderResponse.accepted(receipt);
    }
}

