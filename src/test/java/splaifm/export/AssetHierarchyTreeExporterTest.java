package splaifm.export;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import splaifm.analysis.AssetHierarchyCandidate;
import splaifm.model.Asset;
import splaifm.model.AssetFile;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetHierarchyTreeExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesMainTreeAndAlternativeParents() throws Exception {
        Path output = tempDir.resolve("tree.md");

        new AssetHierarchyTreeExporter().export(model(), candidates(), output);

        String markdown = Files.readString(output);
        assertTrue(markdown.contains("- parentA ParentA.java [type=common, owners=2/3]"));
        assertTrue(markdown.contains(
                "  - child Child.java [type=variable, owners=1/3] -- parent-child, confidence=high"));
        assertTrue(markdown.contains("## Alternative parents"));
        assertTrue(markdown.contains("  - parentB ParentB.java -- parent-child, confidence=medium"));
    }

    private IntegrationModel model() {
        return new IntegrationModel(
                List.of(
                        new Product("Enterprise", "Enterprise"),
                        new Product("Ultimate", "Ultimate"),
                        new Product("Starter", "Starter")),
                List.of(
                        asset("parentA", "common", Set.of("Enterprise", "Ultimate"),
                                "/src/core/ParentA.java"),
                        asset("parentB", "common", Set.of("Enterprise", "Starter"),
                                "/src/ui/ParentB.java"),
                        asset("child", "variable", Set.of("Enterprise"),
                                "/src/core/Child.java")));
    }

    private List<AssetHierarchyCandidate> candidates() {
        return List.of(
                new AssetHierarchyCandidate(
                        "parentA",
                        "ParentA.java",
                        List.of("Enterprise", "Ultimate"),
                        "child",
                        "Child.java",
                        List.of("Enterprise"),
                        "parent-child",
                        "high",
                        "test high candidate"),
                new AssetHierarchyCandidate(
                        "parentB",
                        "ParentB.java",
                        List.of("Enterprise", "Starter"),
                        "child",
                        "Child.java",
                        List.of("Enterprise"),
                        "parent-child",
                        "medium",
                        "test medium candidate"));
    }

    private Asset asset(String id, String type, Set<String> owners, String path) {
        return new Asset(id, type, owners, List.of(new AssetFile(owners.iterator().next(), path)));
    }
}
