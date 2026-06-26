package splaifm.analysis;

import splaifm.model.Asset;
import splaifm.model.AssetFile;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AssetIdentityResolver {

    public List<AssetIdentity> resolveAll(IntegrationModel model) {
        List<String> productOrder = model.products().stream()
                .map(Product::name)
                .toList();
        return model.assets().stream()
                .map(asset -> resolve(asset, productOrder))
                .toList();
    }

    public Map<String, AssetIdentity> indexByAssetId(IntegrationModel model) {
        return resolveAll(model).stream()
                .collect(Collectors.toMap(
                        AssetIdentity::assetId,
                        Function.identity(),
                        (first, second) -> first));
    }

    private AssetIdentity resolve(Asset asset, List<String> productOrder) {
        String representativePath = representativePath(asset);
        return new AssetIdentity(
                asset.id(),
                fileName(representativePath),
                representativePath,
                asset.type(),
                ProductSets.ordered(asset.ownerProductIds(), productOrder));
    }

    private String representativePath(Asset asset) {
        return asset.files().stream()
                .map(AssetFile::path)
                .filter(path -> !path.isBlank())
                .min(Comparator.naturalOrder())
                .orElse("");
    }

    private String fileName(String path) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slash >= 0 ? path.substring(slash + 1) : path;
    }
}
