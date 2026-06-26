package splaifm.export;

import com.fasterxml.jackson.databind.ObjectMapper;
import splaifm.analysis.AssetHierarchyCandidate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class AssetHierarchyJsonExporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void export(List<AssetHierarchyCandidate> candidates, Path outputFile)
            throws IOException {
        Path parent = outputFile.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputFile.toFile(), candidates);
    }
}
