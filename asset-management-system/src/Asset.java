
public class Asset {
    
    private int assetId;
    private String assetName;
    private String assetType;
    private double purchasePrice;
    private int purchaseYear;
    private String status;
    private String assignedTo;

    
    public Asset(int assetId, String assetName, String assetType,
                 double purchasePrice, int purchaseYear,
                 String status, String assignedTo) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.assetType = assetType;
        this.purchasePrice = purchasePrice;
        this.purchaseYear = purchaseYear;
        this.status = status;
        this.assignedTo = assignedTo;
    }

  

    public int getAssetId() {
        return assetId;
    }

    public String getAssetName() {
        return assetName;
    }

    public String getAssetType() {
        return assetType;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public int getPurchaseYear() {
        return purchaseYear;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    // ---------- Setters ----------

    public void setAssetId(int assetId) {
        this.assetId = assetId;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public void setPurchaseYear(int purchaseYear) {
        this.purchaseYear = purchaseYear;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    
    @Override
    public String toString() {
        return assetId + " | " + assetName + " | " + assetType
                + " | " + purchasePrice + " | " + purchaseYear
                + " | " + status + " | " + assignedTo;
    }
}
