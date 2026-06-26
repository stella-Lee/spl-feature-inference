package splaifm.model;

import java.util.Objects;

public record Product(String id, String name) {

    public Product {
        Objects.requireNonNull(id, "id must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        name = name == null || name.isBlank() ? id : name;
    }
}
