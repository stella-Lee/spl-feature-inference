package splaifm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import splaifm.analysis.AssetOwnershipMatrix;
import splaifm.analysis.AssetOwnershipMatrixBuilder;
import splaifm.export.CsvExporter;
import splaifm.model.IntegrationModel;
import splaifm.parser.ModelXmlParser;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssetOwnershipMatrixRegressionTest {

    @TempDir
    Path tempDir;

    @Test
    void generatedMatrixMatchesExpectedCsv() throws Exception {
        IntegrationModel model = new ModelXmlParser().parse(Path.of("input", "model.xml"));
        AssetOwnershipMatrix matrix = new AssetOwnershipMatrixBuilder().build(model);

        Path actual = tempDir.resolve("asset_ownership_matrix.csv");
        new CsvExporter().export(matrix, actual);

        String expectedCsv = Files.readString(
                Path.of("expected", "asset_ownership_matrix_expected.csv"));
        String actualCsv = Files.readString(actual);

        assertEquals(expectedCsv, actualCsv);
    }
}
