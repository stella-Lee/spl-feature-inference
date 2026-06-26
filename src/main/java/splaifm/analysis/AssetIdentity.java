package splaifm.analysis;

import java.util.List;

public record AssetIdentity(
        String assetId,
        String representativeFileName,
        String representativePath,
        String type,
        List<String> owners) {

    public AssetIdentity {
        owners = List.copyOf(owners);
    }

    public int ownerCount() {
        return owners.size();
    }
}
