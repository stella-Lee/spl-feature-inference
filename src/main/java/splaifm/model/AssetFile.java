package splaifm.model;

import java.util.Objects;

public record AssetFile(String owner, String path) {

    public AssetFile {
        Objects.requireNonNull(owner, "owner must not be null");
        if (owner.isBlank()) {
            throw new IllegalArgumentException("owner must not be blank");
        }
        path = path == null ? "" : path;
    }
}
