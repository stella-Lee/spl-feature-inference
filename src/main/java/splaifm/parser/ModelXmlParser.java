package splaifm.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import splaifm.model.Asset;
import splaifm.model.AssetFile;
import splaifm.model.IntegrationModel;
import splaifm.model.Product;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ModelXmlParser {

    public IntegrationModel parse(Path modelXml) throws IOException {
        if (!Files.isRegularFile(modelXml)) {
            throw new IOException("Model XML does not exist or is not a file: " + modelXml);
        }

        try {
            DocumentBuilderFactory factory = secureDocumentBuilderFactory();
            Document document = factory.newDocumentBuilder().parse(modelXml.toFile());
            document.getDocumentElement().normalize();
            return new IntegrationModel(parseProducts(document), parseAssets(document));
        } catch (ParserConfigurationException | SAXException exception) {
            throw new IOException("Unable to parse model XML: " + modelXml, exception);
        }
    }

    private DocumentBuilderFactory secureDocumentBuilderFactory()
            throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setExpandEntityReferences(false);
        factory.setXIncludeAware(false);
        return factory;
    }

    private List<Product> parseProducts(Document document) throws IOException {
        Element productsElement = firstDirectChild(document.getDocumentElement(), "products");
        if (productsElement == null) {
            return List.of();
        }

        List<Product> products = new ArrayList<>();
        for (Element element : directChildren(productsElement, "product")) {
            String id = firstNonBlankAttribute(element, "id", "productId", "name");
            if (id == null) {
                throw new IOException("A product element is missing an id");
            }
            products.add(new Product(id, firstNonBlankAttribute(element, "name")));
        }
        return products;
    }

    private List<Asset> parseAssets(Document document) throws IOException {
        Element assetsElement = firstDirectChild(document.getDocumentElement(), "assets");
        if (assetsElement == null) {
            return List.of();
        }

        List<Asset> assets = new ArrayList<>();
        for (Element element : directChildren(assetsElement, "asset")) {
            String id = firstNonBlankAttribute(element, "id", "assetId", "name");
            if (id == null) {
                throw new IOException("An asset element is missing an id");
            }
            String type = firstNonBlankAttribute(element, "type", "assetType");
            List<AssetFile> files = parseAssetFiles(element);
            Set<String> ownerProductIds = files.stream()
                    .map(AssetFile::owner)
                    .collect(java.util.stream.Collectors.toUnmodifiableSet());
            assets.add(new Asset(id, type, ownerProductIds, files));
        }
        return assets;
    }

    private List<AssetFile> parseAssetFiles(Element assetElement) {
        List<AssetFile> files = new ArrayList<>();
        for (Element fileElement : directChildren(assetElement, "file")) {
            String owner = firstNonBlankAttribute(fileElement, "owner");
            if (owner != null) {
                files.add(new AssetFile(owner, firstNonBlankAttribute(fileElement, "path")));
            }
        }
        return files;
    }

    private Element firstDirectChild(Element parent, String tagName) {
        return directChildren(parent, tagName).stream().findFirst().orElse(null);
    }

    private List<Element> directChildren(Element parent, String tagName) {
        return directChildElements(parent).stream()
                .filter(element -> element.getTagName().equals(tagName))
                .toList();
    }

    private List<Element> directChildElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int index = 0; index < children.getLength(); index++) {
            Node node = children.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) node);
            }
        }
        return elements;
    }

    private String firstNonBlankAttribute(Element element, String... attributeNames) {
        for (String attributeName : attributeNames) {
            String value = element.getAttribute(attributeName);
            if (!value.isBlank()) {
                return value.trim();
            }
        }
        return null;
    }
}
