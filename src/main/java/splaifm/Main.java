package splaifm;

import splaifm.analysis.AssetHierarchyCandidateBuilder;
import splaifm.analysis.AssetIdentityBuilder;
import splaifm.analysis.AssetOwnershipMatrix;
import splaifm.analysis.AssetOwnershipMatrixBuilder;
import splaifm.export.AssetHierarchyDotExporter;
import splaifm.export.AssetHierarchyJsonExporter;
import splaifm.export.AssetHierarchyTreeExporter;
import splaifm.export.CsvExporter;
import splaifm.model.IntegrationModel;
import splaifm.parser.ModelXmlParser;

import java.nio.file.Path;

public final class Main {

    private static final Path INPUT_MODEL = Path.of("input", "model.xml");
    private static final Path OUTPUT_CSV =
            Path.of("output", "asset_ownership_matrix.csv");
    private static final Path ASSET_IDENTITY_CSV =
            Path.of("output", "asset_identity.csv");
    private static final Path HIERARCHY_CANDIDATES_CSV =
            Path.of("output", "asset_hierarchy_candidates.csv");
    private static final Path HIERARCHY_TREE_MD =
            Path.of("output", "asset_hierarchy_tree.md");
    private static final Path HIERARCHY_GRAPH_DOT =
            Path.of("output", "asset_hierarchy_graph.dot");
    private static final Path HIERARCHY_CANDIDATES_JSON =
            Path.of("output", "asset_hierarchy_candidates.json");

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        IntegrationModel model = new ModelXmlParser().parse(INPUT_MODEL);
        AssetOwnershipMatrix matrix =
                new AssetOwnershipMatrixBuilder().build(model);
        new CsvExporter().export(matrix, OUTPUT_CSV);

        AssetOwnershipMatrix assetIdentity =
                new AssetIdentityBuilder().build(model);
        new CsvExporter().export(assetIdentity, ASSET_IDENTITY_CSV);

        AssetHierarchyCandidateBuilder hierarchyCandidateBuilder =
                new AssetHierarchyCandidateBuilder();
        var hierarchyCandidateList = hierarchyCandidateBuilder.buildCandidates(model);
        AssetOwnershipMatrix hierarchyCandidates =
                hierarchyCandidateBuilder.build(model);
        new CsvExporter().export(hierarchyCandidates, HIERARCHY_CANDIDATES_CSV);
        new AssetHierarchyTreeExporter().export(
                model, hierarchyCandidateList, HIERARCHY_TREE_MD);
        new AssetHierarchyDotExporter().export(
                model, hierarchyCandidateList, HIERARCHY_GRAPH_DOT);
        new AssetHierarchyJsonExporter().export(
                hierarchyCandidateList, HIERARCHY_CANDIDATES_JSON);

        System.out.printf(
                "Wrote %d asset rows to %s%n",
                matrix.rows().size(),
                OUTPUT_CSV.toAbsolutePath());
        System.out.printf(
                "Wrote %d asset identity rows to %s%n",
                assetIdentity.rows().size(),
                ASSET_IDENTITY_CSV.toAbsolutePath());
        System.out.printf(
                "Wrote %d hierarchy candidate rows to %s%n",
                hierarchyCandidates.rows().size(),
                HIERARCHY_CANDIDATES_CSV.toAbsolutePath());
        System.out.printf(
                "Wrote hierarchy tree to %s%n",
                HIERARCHY_TREE_MD.toAbsolutePath());
        System.out.printf(
                "Wrote hierarchy graph to %s%n",
                HIERARCHY_GRAPH_DOT.toAbsolutePath());
        System.out.printf(
                "Wrote hierarchy candidate JSON to %s%n",
                HIERARCHY_CANDIDATES_JSON.toAbsolutePath());
    }
}
