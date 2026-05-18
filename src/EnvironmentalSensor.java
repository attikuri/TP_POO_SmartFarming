public class EnvironmentalSensor extends NumericalSensor {
    private EnvironmentalType type;

    public EnvironmentalSensor(int  code, int  zoneCode, EnvironmentalType type,
                               double minThreshold, double maxThreshold) throws ThresholdException {
        // choose unit automatically based on type
        super(code, zoneCode, decideUnit(type), minThreshold, maxThreshold);
        this.type = type;
    }

    // decides the unit based on the type
    private static NumericalSensor.Unit decideUnit(EnvironmentalType type) {
        switch (type) {
            case TEMPERATURE: return NumericalSensor.Unit.CELSIUS;
            case HUMIDITY:    return NumericalSensor.Unit.PERCENTAGE;
            case RAINFALL:    return NumericalSensor.Unit.MM;
            default:          return null;
        }
    }
    public EnvironmentalType getType() { return type; }
    @Override
    public String getTypeAsString() {
        return type.toString(); // transform the enum types to strings
    }
}