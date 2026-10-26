import java.util.ArrayList;
import java.util.List;


public class AssetManager {

    // Data store: List of Asset objects
    private final List<Asset> assets;

    // Constructor
    public AssetManager() {
        this.assets = new ArrayList<>();
    }

    /**
     * Returns the underlying list of assets (useful for testing).
     */
    public List<Asset> getAssets() {
        return assets;
    }

   
    public boolean addAsset(Asset asset) {
        // ID uniqueness check
        for (Asset existing : assets) {
            if (existing.getAssetId() == asset.getAssetId()) {
                return false; // Duplicate asset ID
            }
        }

        // Required-field validation
        if (asset.getAssetName() == null || asset.getAssetName().trim().isEmpty()) {
            return false;
        }
        if (asset.getAssetType() == null || asset.getAssetType().trim().isEmpty()) {
            return false;
        }
        if (asset.getStatus() == null || asset.getStatus().trim().isEmpty()) {
            return false;
        }
        if (asset.getAssignedTo() == null || asset.getAssignedTo().trim().isEmpty()) {
            return false;
        }
        if (asset.getPurchasePrice() < 0) {
            return false;
        }

        assets.add(asset);
        return true;
    }

   
    public List<Asset> viewAllAssets() {
        return new ArrayList<>(assets);
    }

    
    public void displayAllAssets() {
        if (assets.isEmpty()) {
            System.out.println("\nNo assets found in the system.\n");
            return;
        }

        System.out.println("\n-----------------------------------");
        System.out.printf("%-6s %-18s %-12s %-10s %-8s%n",
                "ID", "Name", "Type", "Price", "Status");
        System.out.println("-----------------------------------");

        for (Asset a : assets) {
            System.out.printf("%-6d %-18s %-12s %-10.2f %-8s%n",
                    a.getAssetId(),
                    a.getAssetName(),
                    a.getAssetType(),
                    a.getPurchasePrice(),
                    a.getStatus());
        }
        System.out.println("-----------------------------------\n");
    }

    
    public Asset searchByAssetId(int assetId) {
        for (Asset a : assets) {
            if (a.getAssetId() == assetId) {
                return a;
            }
        }
        return null;
    }

   
    public List<Asset> searchByName(String name) {
        List<Asset> results = new ArrayList<>();
        String lower = name.toLowerCase();
        for (Asset a : assets) {
            if (a.getAssetName().toLowerCase().contains(lower)) {
                results.add(a);
            }
        }
        return results;
    }

    
    public List<Asset> searchByType(String type) {
        List<Asset> results = new ArrayList<>();
        String lower = type.toLowerCase();
        for (Asset a : assets) {
            if (a.getAssetType().toLowerCase().contains(lower)) {
                results.add(a);
            }
        }
        return results;
    }

   
    public boolean updateAsset(int assetId, Asset asset) {
        for (Asset a : assets) {
            if (a.getAssetId() == assetId) {
                if (asset.getAssetName() != null) {
                    a.setAssetName(asset.getAssetName());
                }
                if (asset.getAssetType() != null) {
                    a.setAssetType(asset.getAssetType());
                }
                if (asset.getPurchasePrice() >= 0) {
                    a.setPurchasePrice(asset.getPurchasePrice());
                }
                if (asset.getPurchaseYear() > 0) {
                    a.setPurchaseYear(asset.getPurchaseYear());
                }
                if (asset.getStatus() != null && !asset.getStatus().isEmpty()) {
                    a.setStatus(asset.getStatus());
                }
                if (asset.getAssignedTo() != null && !asset.getAssignedTo().isEmpty()) {
                    a.setAssignedTo(asset.getAssignedTo());
                }
                return true;
            }
        }
        return false; // Asset not found
    }

    // ---------- Delete Asset ----------

   
    public boolean deleteAsset(int assetId) {
        for (Asset a : assets) {
            if (a.getAssetId() == assetId) {
                return assets.remove(a);
            }
        }
        return false; // Asset not found
    }
}
