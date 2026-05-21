public class HarvestProductionRecord extends ProductionRecord {
    public HarvestProductionRecord(String zoneId, double weightInKg, double expectedMinimumThreshold) {
        super(zoneId, weightInKg, expectedMinimumThreshold);
    }

    @Override
    public String getUnit() { return "Unit: Kg"; }
}