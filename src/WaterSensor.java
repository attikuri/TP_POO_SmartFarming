public class WaterSensor extends NumericalSensor{
    private WaterType type;

    public WaterSensor(int code, int zoneCode, WaterType type,
                       double minThreshold, double maxThreshold) throws ThresholdException {
        super(code, zoneCode, decideUnit(type), minThreshold, maxThreshold);
        this.type = type;
    }

    private static NumericalSensor.Unit decideUnit(WaterType type) {
        switch (type) {
            case TEMPERATURE:  return NumericalSensor.Unit.CELSIUS;
            case DISSOLVED_O2: return NumericalSensor.Unit.MG_PER_L;
            case WATER_LEVEL:  return NumericalSensor.Unit.PERCENTAGE;
            default:           return null;
        }
    }

    public WaterType getType() { return type; }
    // in WaterSensor
    @Override
    public String getTypeAsString() {
        return type.toString();
    }

}