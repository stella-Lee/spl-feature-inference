package splaifm.analysis;

import org.junit.jupiter.api.Test;
import splaifm.model.Asset;
import splaifm.model.AssetFile;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssetHierarchyCandidateBuilderTest {

    @Test
    void createsCandidatesOnlyForProperProductSetSubsets() {
        IntegrationModel model = new IntegrationModel(
                List.of(
                        new Product("Enterprise", "Enterprise"),
                        new Product("Ultimate", "Ultimate"),
                        new Product("Starter", "Starter")),
                List.of(
                        asset("parent", "common",
                                Set.of("Enterprise", "Ultimate"),
                                "/src/core/controller/ControlUnit.java"),
                        asset("child", "variable",
                                Set.of("Enterprise"),
                                "/src/core/controller/Request.java"),
                        asset("equal", "variable",
                                Set.of("Enterprise", "Ultimate"),
                                "/src/core/controller/Elevator.java"),
                        asset("disjoint", "variable",
                                Set.of("Starter"),
                                "/src/ui/StarterOnly.java")));

        AssetOwnershipMatrix matrix = new AssetHierarchyCandidateBuilder().build(model);

        assertEquals(2, matrix.rows().size());
        List<String> row = matrix.rows().get(0);
        assertEquals("parent", row.get(0));
        assertEquals("ControlUnit.java", row.get(1));
        assertEquals("Enterprise;Ultimate", row.get(2));
        assertEquals("child", row.get(3));
        assertEquals("Request.java", row.get(4));
        assertEquals("Enterprise", row.get(5));
        assertEquals("parent-child", row.get(6));
        assertEquals("high", row.get(7));
        assertTrue(row.get(8).contains("proper subset"));
        assertTrue(row.get(8).contains("same representative directory"));
        assertTrue(matrix.rows().stream()
                .noneMatch(candidate -> candidate.get(0).equals("parent")
                        && candidate.get(3).equals("equal")));
        assertTrue(matrix.rows().stream()
                .noneMatch(candidate -> candidate.get(3).equals("disjoint")));
    }

    @Test
    void usesMediumConfidenceWhenContainmentHasNoStrongSemanticEvidence() {
        IntegrationModel model = new IntegrationModel(
                List.of(
                        new Product("Enterprise", "Enterprise"),
                        new Product("Ultimate", "Ultimate")),
                List.of(
                        asset("parent", "common",
                                Set.of("Enterprise", "Ultimate"),
                                "/src/core/controller/ControlUnit.java"),
                        asset("child", "variable",
                                Set.of("Enterprise"),
                                "/resources/images/DoorIcon.png")));

        AssetOwnershipMatrix matrix = new AssetHierarchyCandidateBuilder().build(model);

        assertEquals(1, matrix.rows().size());
        assertEquals("medium", matrix.rows().get(0).get(7));
        assertTrue(matrix.rows().get(0).get(8).contains("no strong filename/path similarity"));
    }

    private Asset asset(String id, String type, Set<String> owners, String representativePath) {
        return new Asset(
                id,
                type,
                owners,
                List.of(new AssetFile(owners.iterator().next(), representativePath)));
    }
}
