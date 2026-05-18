public class AquacultureZone extends GeographicalZone {
    private Aquaculture tank;
    private FeedingPgm program;

    public AquacultureZone(String code, String name, ZoneStatus status, Aquaculture basin, FeedingPgm program) {
        super(code, name, status);

        this.tank = tank;
        this.program = program;
    }

    public FeedingPgm getFeedingProgram() {
        return program;
    }

    public void setFeedingProgram(FeedingPgm newProgram) {
        this.program = newProgram;
    }

    public void addWaterSensor(WaterSensor waterSensor) {
         this.sensors.add(waterSensor);
    }

    @Override
    public int getEntityCount() {
        if (tank != null) {
            return tank.getAnimalCount();
        }
        return 0;
    }
}