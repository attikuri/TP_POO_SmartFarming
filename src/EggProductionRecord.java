public class EggProductionRecord extends ProductionRecord {
    public EggProductionRecord(String zoneId, int numberOfEggs, double expectedMinimumThreshold) {
        super(zoneId, (double) numberOfEggs, expectedMinimumThreshold);
    }

    @Override
    public String getUnit() {
        return "Unity: eggs";
    }
}