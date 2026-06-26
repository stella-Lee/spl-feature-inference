package splaifm.parser;

import org.junit.jupiter.api.Test;
import splaifm.model.IntegrationModel;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModelXmlParserTest {

    private final ModelXmlParser parser = new ModelXmlParser();

    @Test
    void parsesProductsAssetsAndOwnership() throws Exception {
        IntegrationModel model = parser.parse(fixturePath());

        assertEquals(3, model.products().size());
        assertEquals("enterprise", model.products().get(0).id());
        assertEquals("Enterprise", model.products().get(0).name());

        assertEquals(2, model.assets().size());
        assertEquals(
                Set.of("Enterprise", "Professional"),
                model.assets().get(0).ownerProductIds());
        assertEquals(
                Set.of("Enterprise", "HomeBasic"),
                model.assets().get(1).ownerProductIds());
    }

    @Test
    void parsesAssetFileOwnerMetadataWithoutReadingFileContents() throws Exception {
        IntegrationModel model = parser.parse(fixturePath());

        assertTrue(model.assets().stream().allMatch(asset -> !asset.files().isEmpty()));
        assertEquals("Enterprise", model.assets().get(0).files().get(0).owner());
        assertEquals("/src/main/java/example/App.java", model.assets().get(0).files().get(0).path());
    }

    private Path fixturePath() throws URISyntaxException {
        return Path.of(
                ModelXmlParserTest.class.getResource("/model-small.xml").toURI());
    }
}
