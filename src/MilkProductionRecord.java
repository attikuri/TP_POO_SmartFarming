public class MilkProductionRecord extends ProductionRecord {
    public MilkProductionRecord(String zoneId, double litersOfMilk, double expectedMinimumThreshold) {
        super(zoneId, litersOfMilk, expectedMinimumThreshold);
    }

    @Override
    public String getUnit() {
        return "Litres (Lait)";
    }
}