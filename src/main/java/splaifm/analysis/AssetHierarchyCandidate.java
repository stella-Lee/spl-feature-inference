package splaifm.analysis;

import java.util.List;

public record AssetHierarchyCandidate(
        String parentAssetId,
        String parentName,
        List<String> parentProductSet,
        String childAssetId,
        String childName,
        List<String> childProductSet,
        String relationCandidate,
        String confidence,
        String evidence) {

    public AssetHierarchyCandidate {
        parentProductSet = List.copyOf(parentProductSet);
        childProductSet = List.copyOf(childProductSet);
    }
}
