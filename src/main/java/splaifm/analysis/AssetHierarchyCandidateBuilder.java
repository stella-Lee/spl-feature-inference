package splaifm.analysis;

import splaifm.model.Asset;
import splaifm.model.AssetFile;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class AssetHierarchyCandidateBuilder {

    public AssetOwnershipMatrix build(IntegrationModel model) {
        List<List<String>> rows = buildCandidates(model).stream()
                .map(this::buildRow)
                .toList();

        return new AssetOwnershipMatrix(
                List.of(
                        "parentAssetId",
                        "parentName",
                        "parentProductSet",
                        "childAssetId",
                        "childName",
                        "childProductSet",
                        "relationCandidate",
                        "confidence",
                        "evidence"),
                rows);
    }

    public List<AssetHierarchyCandidate> buildCandidates(IntegrationModel model) {
        List<String> productOrder = model.products().stream()
                .map(Product::name)
                .toList();
        return model.assets().stream()
                .flatMap(parent -> model.assets().stream()
                        .filter(child -> !parent.id().equals(child.id()))
                        .filter(child -> ProductSets.isProperSubset(
                                child.ownerProductIds(), parent.ownerProductIds()))
                        .map(child -> buildCandidate(parent, child, productOrder)))
                .toList();
    }

    private AssetHierarchyCandidate buildCandidate(
            Asset parent,
            Asset child,
            List<String> productOrder) {
        AssetIdentity parentIdentity = identity(parent);
        AssetIdentity childIdentity = identity(child);
        SemanticEvidence semanticEvidence = semanticEvidence(parentIdentity, childIdentity);

        String confidence = semanticEvidence.related() ? "high" : "medium";
        String evidence = "child product set is a proper subset of parent product set"
                + "; parentOwners=" + ProductSets.format(parent.ownerProductIds(), productOrder)
                + "; childOwners=" + ProductSets.format(child.ownerProductIds(), productOrder)
                + "; semanticEvidence=" + semanticEvidence.description();

        return new AssetHierarchyCandidate(
                parent.id(),
                parentIdentity.name(),
                ProductSets.ordered(parent.ownerProductIds(), productOrder),
                child.id(),
                childIdentity.name(),
                ProductSets.ordered(child.ownerProductIds(), productOrder),
                "parent-child",
                confidence,
                evidence);
    }

    private List<String> buildRow(AssetHierarchyCandidate candidate) {
        return List.of(
                candidate.parentAssetId(),
                candidate.parentName(),
                String.join(";", candidate.parentProductSet()),
                candidate.childAssetId(),
                candidate.childName(),
                String.join(";", candidate.childProductSet()),
                candidate.relationCandidate(),
                candidate.confidence(),
                candidate.evidence());
    }

    private AssetIdentity identity(Asset asset) {
        String representativePath = asset.files().stream()
                .map(AssetFile::path)
                .filter(path -> !path.isBlank())
                .sorted()
                .findFirst()
                .orElse("");
        return new AssetIdentity(fileName(representativePath), representativePath);
    }

    private SemanticEvidence semanticEvidence(AssetIdentity parent, AssetIdentity child) {
        if (parent.path().isBlank() || child.path().isBlank()) {
            return new SemanticEvidence(false, "missing representative path");
        }

        if (directory(parent.path()).equals(directory(child.path()))) {
            return new SemanticEvidence(true, "same representative directory");
        }

        Set<String> sharedTokens = new LinkedHashSet<>(tokens(parent.name()));
        sharedTokens.retainAll(tokens(child.name()));
        if (!sharedTokens.isEmpty()) {
            return new SemanticEvidence(
                    true,
                    "shared filename tokens: " + String.join(";", sharedTokens));
        }

        return new SemanticEvidence(false, "no strong filename/path similarity");
    }

    private String fileName(String path) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slash >= 0 ? path.substring(slash + 1) : path;
    }

    private String directory(String path) {
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return slash >= 0 ? path.substring(0, slash) : "";
    }

    private Set<String> tokens(String value) {
        String spacedCamelCase = value.replaceAll("([a-z])([A-Z])", "$1 $2");
        return Arrays.stream(spacedCamelCase.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(token -> token.length() > 2)
                .filter(token -> !Set.of(
                        "java",
                        "src",
                        "main",
                        "test",
                        "class",
                        "file").contains(token))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private record AssetIdentity(String name, String path) {
    }

    private record SemanticEvidence(boolean related, String description) {
    }
}
