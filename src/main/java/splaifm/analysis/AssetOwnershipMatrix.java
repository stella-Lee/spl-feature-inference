package splaifm.analysis;

import java.util.List;

public record AssetOwnershipMatrix(List<String> headers, List<List<String>> rows) {

    public AssetOwnershipMatrix {
        headers = List.copyOf(headers);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
