import java.time.LocalDate;

/**
 * ProductionRecord — abstract base for all zone production entries.
 *
 * Fix applied: added getRecordDate() and getExpectedMinimumThreshold() public
 * getters so the Farm GUI and simulation panel can read them without reflection.
 */
public abstract class ProductionRecord {
    protected String     zoneId;
    protected double     productionValue;
    protected LocalDate  recordDate;
    protected double     expectedMinimumThreshold;

    public ProductionRecord(String zoneId, double productionValue,
                            double expectedMinimumThreshold) {
        if (productionValue < 0)
            throw new IllegalArgumentException("Production value cannot be negative.");
        this.zoneId                   = zoneId;
        this.productionValue          = productionValue;
        this.expectedMinimumThreshold = expectedMinimumThreshold;
        this.recordDate               = LocalDate.now();
        evaluateAndNotify();
    }

    private void evaluateAndNotify() {
        if (productionValue < expectedMinimumThreshold) {
            System.out.println("LOG/ALERT: Critical production drop in zone " + zoneId
                    + " (" + productionValue + " < " + expectedMinimumThreshold + ")");
        }
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String    getZoneId()                    { return zoneId; }
    public double    getProductionValue()           { return productionValue; }
    public LocalDate getRecordDate()                { return recordDate; }
    public double    getExpectedMinimumThreshold()  { return expectedMinimumThreshold; }

    /** Human-readable unit string, e.g. "Litres (Lait)" or "Unit: Kg". */
    public abstract String getUnit();
}