package splaifm.analysis;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductSetsTest {

    @Test
    void detectsProperSubsetContainment() {
        assertTrue(ProductSets.isProperSubset(
                Set.of("Enterprise", "Ultimate"),
                Set.of("Enterprise", "HomeBasic", "Ultimate")));
    }

    @Test
    void rejectsEqualDisjointAndEmptySets() {
        assertFalse(ProductSets.isProperSubset(
                Set.of("Enterprise", "Ultimate"),
                Set.of("Enterprise", "Ultimate")));
        assertFalse(ProductSets.isProperSubset(
                Set.of("Starter"),
                Set.of("Enterprise", "Ultimate")));
        assertFalse(ProductSets.isProperSubset(
                Set.of(),
                Set.of("Enterprise", "Ultimate")));
    }
}
