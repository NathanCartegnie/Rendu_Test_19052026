package org.example;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findByReference(String reference);
}

