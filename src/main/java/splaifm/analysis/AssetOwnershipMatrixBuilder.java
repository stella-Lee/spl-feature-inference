package splaifm.analysis;

import splaifm.model.Asset;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import java.util.ArrayList;
import java.util.List;

public final class AssetOwnershipMatrixBuilder {

    public AssetOwnershipMatrix build(IntegrationModel model) {
        List<String> productColumns = model.products().stream()
                .map(Product::name)
                .toList();
        List<String> headers = new ArrayList<>(List.of("assetId", "type"));
        headers.addAll(productColumns);

        List<List<String>> rows = model.assets().stream()
                .map(asset -> buildRow(asset, productColumns))
                .toList();
        return new AssetOwnershipMatrix(headers, rows);
    }

    private List<String> buildRow(
            Asset asset,
            List<String> productColumns) {
        List<String> row = new ArrayList<>();
        row.add(asset.id());
        row.add(asset.type());
        for (String productColumn : productColumns) {
            boolean owned = asset.ownerProductIds().contains(productColumn);
            row.add(owned ? "1" : "0");
        }
        return row;
    }
}
