package splaifm.export;

import splaifm.analysis.AssetHierarchyCandidate;
import splaifm.analysis.AssetIdentity;
import splaifm.analysis.AssetIdentityResolver;
import splaifm.model.IntegrationModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class AssetHierarchyTreeExporter {

    public void export(
            IntegrationModel model,
            List<AssetHierarchyCandidate> candidates,
            Path outputFile) throws IOException {
        List<AssetIdentity> identities = new AssetIdentityResolver().resolveAll(model);
        Map<String, AssetIdentity> identitiesById = indexIdentities(identities);
        Map<String, AssetHierarchyCandidate> selectedParents = selectBestParents(candidates);
        Map<String, List<AssetHierarchyCandidate>> childrenByParent =
                groupSelectedChildren(selectedParents);
        int totalProductCount = model.products().size();

        StringBuilder markdown = new StringBuilder();
        markdown.append("# Asset Hierarchy Candidate Tree\n\n");
        markdown.append("This is an asset-based candidate hierarchy, not a final feature model.\n\n");
        markdown.append("## Main tree\n\n");

        Set<String> selectedChildIds = selectedParents.keySet();
        for (AssetIdentity identity : identities) {
            if (!selectedChildIds.contains(identity.assetId())) {
                appendNode(markdown, identity, null, childrenByParent, identitiesById,
                        totalProductCount, 0);
            }
        }

        appendAlternativeParents(markdown, candidates, selectedParents, identitiesById);

        writeString(outputFile, markdown.toString());
    }

    private void appendNode(
            StringBuilder markdown,
            AssetIdentity identity,
            AssetHierarchyCandidate incomingEdge,
            Map<String, List<AssetHierarchyCandidate>> childrenByParent,
            Map<String, AssetIdentity> identitiesById,
            int totalProductCount,
            int depth) {
        markdown.append("  ".repeat(depth))
                .append("- ")
                .append(formatNode(identity, totalProductCount));
        if (incomingEdge != null) {
            markdown.append(" -- ")
                    .append(incomingEdge.relationCandidate())
                    .append(", confidence=")
                    .append(incomingEdge.confidence());
        }
        markdown.append("\n");

        for (AssetHierarchyCandidate childEdge :
                childrenByParent.getOrDefault(identity.assetId(), List.of())) {
            AssetIdentity child = identitiesById.get(childEdge.childAssetId());
            if (child != null) {
                appendNode(markdown, child, childEdge, childrenByParent, identitiesById,
                        totalProductCount, depth + 1);
            }
        }
    }

    private void appendAlternativeParents(
            StringBuilder markdown,
            List<AssetHierarchyCandidate> candidates,
            Map<String, AssetHierarchyCandidate> selectedParents,
            Map<String, AssetIdentity> identitiesById) {
        Map<String, List<AssetHierarchyCandidate>> alternatives = new LinkedHashMap<>();
        for (AssetHierarchyCandidate candidate : candidates) {
            AssetHierarchyCandidate selected = selectedParents.get(candidate.childAssetId());
            if (selected != candidate) {
                alternatives.computeIfAbsent(candidate.childAssetId(), ignored -> new ArrayList<>())
                        .add(candidate);
            }
        }

        if (alternatives.isEmpty()) {
            markdown.append("\n## Alternative parents\n\nNone.\n");
            return;
        }

        markdown.append("\n## Alternative parents\n\n");
        for (Map.Entry<String, List<AssetHierarchyCandidate>> entry : alternatives.entrySet()) {
            AssetIdentity child = identitiesById.get(entry.getKey());
            markdown.append("- ")
                    .append(child == null ? entry.getKey() : child.assetId() + " "
                            + child.representativeFileName())
                    .append("\n");
            for (AssetHierarchyCandidate alternative : entry.getValue()) {
                markdown.append("  - ")
                        .append(alternative.parentAssetId())
                        .append(" ")
                        .append(alternative.parentName())
                        .append(" -- ")
                        .append(alternative.relationCandidate())
                        .append(", confidence=")
                        .append(alternative.confidence())
                        .append("\n");
            }
        }
    }

    static Map<String, AssetHierarchyCandidate> selectBestParents(
            List<AssetHierarchyCandidate> candidates) {
        Map<String, AssetHierarchyCandidate> selectedParents = new LinkedHashMap<>();
        for (AssetHierarchyCandidate candidate : candidates) {
            AssetHierarchyCandidate current = selectedParents.get(candidate.childAssetId());
            if (current == null || confidenceRank(candidate.confidence())
                    > confidenceRank(current.confidence())) {
                selectedParents.put(candidate.childAssetId(), candidate);
            }
        }
        return selectedParents;
    }

    private Map<String, List<AssetHierarchyCandidate>> groupSelectedChildren(
            Map<String, AssetHierarchyCandidate> selectedParents) {
        return selectedParents.values().stream()
                .collect(Collectors.groupingBy(
                        AssetHierarchyCandidate::parentAssetId,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    private Map<String, AssetIdentity> indexIdentities(List<AssetIdentity> identities) {
        Map<String, AssetIdentity> identitiesById = new LinkedHashMap<>();
        for (AssetIdentity identity : identities) {
            identitiesById.put(identity.assetId(), identity);
        }
        return identitiesById;
    }

    private String formatNode(AssetIdentity identity, int totalProductCount) {
        return identity.assetId()
                + " "
                + identity.representativeFileName()
                + " [type="
                + identity.type()
                + ", owners="
                + identity.ownerCount()
                + "/"
                + totalProductCount
                + "]";
    }

    private static int confidenceRank(String confidence) {
        return switch (confidence) {
            case "high" -> 3;
            case "medium" -> 2;
            case "low" -> 1;
            default -> 0;
        };
    }

    private void writeString(Path outputFile, String content) throws IOException {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(outputFile, content, StandardCharsets.UTF_8);
    }
}
