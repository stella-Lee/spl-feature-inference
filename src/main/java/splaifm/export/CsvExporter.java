package splaifm.export;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import splaifm.analysis.AssetOwnershipMatrix;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CsvExporter {

    public void export(AssetOwnershipMatrix matrix, Path outputFile) throws IOException {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(matrix.headers().toArray(String[]::new))
                .setRecordSeparator("\n")
                .build();

        try (BufferedWriter writer = Files.newBufferedWriter(
                outputFile, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (var row : matrix.rows()) {
                printer.printRecord(row);
            }
        }
    }
}
