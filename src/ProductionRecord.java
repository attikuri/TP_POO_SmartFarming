import java.time.LocalDate;

public abstract class ProductionRecord {
    protected String zoneId;
    protected double productionValue;
    protected LocalDate recordDate;
    protected double expectedMinimumThreshold; // to detect anomalies.

    public ProductionRecord(String zoneId, double productionValue, double expectedMinimumThreshold) {
        if (productionValue < 0) {
            throw new IllegalArgumentException("Production value cannot be negative.");
        }
        this.zoneId = zoneId;
        this.productionValue = productionValue;
        this.expectedMinimumThreshold = expectedMinimumThreshold;
        this.recordDate = LocalDate.now();
        evaluateAndNotify();
    }

    private void evaluateAndNotify() {
        boolean isAbnormal = productionValue < expectedMinimumThreshold;

        Reading currentReading = new Reading(productionValue, isAbnormal);


        if (isAbnormal) {
            System.out.println("LOG/ALERT: Baisse de production critique dans la zone " + zoneId);
        }
    }

    public String getZoneId() { return zoneId; }

    public abstract String getUnit();
    public double getProductionValue() { return productionValue; }
}