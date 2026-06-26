package splaifm.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Asset(
        String id,
        String type,
        Set<String> ownerProductIds,
        List<AssetFile> files) {

    public Asset {
        Objects.requireNonNull(id, "id must not be null");
        if (id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }
        type = type == null ? "" : type;
        ownerProductIds = Set.copyOf(
                Objects.requireNonNull(ownerProductIds, "ownerProductIds must not be null"));
        files = List.copyOf(Objects.requireNonNull(files, "files must not be null"));
    }
}
