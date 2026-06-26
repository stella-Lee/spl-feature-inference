package splaifm.analysis;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

final class ProductSets {

    private ProductSets() {
    }

    static boolean isProperSubset(Set<String> childProductSet, Set<String> parentProductSet) {
        return !childProductSet.isEmpty()
                && parentProductSet.containsAll(childProductSet)
                && parentProductSet.size() > childProductSet.size();
    }

    static String format(Set<String> productSet, List<String> productOrder) {
        return String.join(";", ordered(productSet, productOrder));
    }

    static List<String> ordered(Set<String> productSet, List<String> productOrder) {
        LinkedHashSet<String> ordered = new LinkedHashSet<>();
        for (String product : productOrder) {
            if (productSet.contains(product)) {
                ordered.add(product);
            }
        }
        productSet.stream()
                .filter(product -> !ordered.contains(product))
                .sorted(Comparator.naturalOrder())
                .forEach(ordered::add);
        return List.copyOf(ordered);
    }
}
