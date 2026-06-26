package splaifm.analysis;

import splaifm.model.IntegrationModel;

import java.util.List;

public final class AssetIdentityBuilder {

    public AssetOwnershipMatrix build(IntegrationModel model) {
        List<List<String>> rows = new AssetIdentityResolver().resolveAll(model).stream()
                .map(this::buildRow)
                .toList();

        return new AssetOwnershipMatrix(
                List.of("assetId", "representativeFileName", "representativePath", "type", "owners"),
                rows);
    }

    private List<String> buildRow(AssetIdentity identity) {
        return List.of(
                identity.assetId(),
                identity.representativeFileName(),
                identity.representativePath(),
                identity.type(),
                String.join(";", identity.owners()));
    }
}
