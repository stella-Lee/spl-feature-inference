package splaifm.model;

import java.util.List;
import java.util.Objects;

public record IntegrationModel(List<Product> products, List<Asset> assets) {

    public IntegrationModel {
        products = List.copyOf(Objects.requireNonNull(products, "products must not be null"));
        assets = List.copyOf(Objects.requireNonNull(assets, "assets must not be null"));
    }
}
