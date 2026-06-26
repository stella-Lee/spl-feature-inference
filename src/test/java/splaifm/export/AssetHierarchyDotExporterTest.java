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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetHierarchyDotExporterTest {

    @TempDir
    Path tempDir;

    @Test
    void writesGraphWithSelectedBestParentEdges() throws Exception {
        Path output = tempDir.resolve("graph.dot");

        new AssetHierarchyDotExporter().export(model(), candidates(), output);

        String dot = Files.readString(output);
        assertTrue(dot.contains("digraph AssetHierarchyCandidates"));
        assertTrue(dot.contains("\"parentA\" [label=\"parentA\\nParentA.java\"]"));
        assertTrue(dot.contains("\"parentA\" -> \"child\" [label=\"high\"]"));
        assertFalse(dot.contains("\"parentB\" -> \"child\""));
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
