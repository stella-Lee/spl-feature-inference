package splaifm.export;

import splaifm.analysis.AssetHierarchyCandidate;
import splaifm.analysis.AssetIdentity;
import splaifm.analysis.AssetIdentityResolver;
import splaifm.model.IntegrationModel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class AssetHierarchyDotExporter {

    public void export(
            IntegrationModel model,
            List<AssetHierarchyCandidate> candidates,
            Path outputFile) throws IOException {
        List<AssetIdentity> identities = new AssetIdentityResolver().resolveAll(model);
        Map<String, AssetHierarchyCandidate> selectedParents =
                AssetHierarchyTreeExporter.selectBestParents(candidates);

        StringBuilder dot = new StringBuilder();
        dot.append("digraph AssetHierarchyCandidates {\n");
        dot.append("  rankdir=TB;\n");
        dot.append("  node [shape=box];\n");

        for (AssetIdentity identity : identities) {
            dot.append("  \"")
                    .append(escape(identity.assetId()))
                    .append("\" [label=\"")
                    .append(escape(identity.assetId()))
                    .append("\\n")
                    .append(escape(identity.representativeFileName()))
                    .append("\"];\n");
        }

        for (AssetHierarchyCandidate candidate : selectedParents.values()) {
            dot.append("  \"")
                    .append(escape(candidate.parentAssetId()))
                    .append("\" -> \"")
                    .append(escape(candidate.childAssetId()))
                    .append("\" [label=\"")
                    .append(escape(candidate.confidence()))
                    .append("\"];\n");
        }

        dot.append("}\n");
        writeString(outputFile, dot.toString());
    }

    private String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private void writeString(Path outputFile, String content) throws IOException {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        Files.writeString(outputFile, content, StandardCharsets.UTF_8);
    }
}
