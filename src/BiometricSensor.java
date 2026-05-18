public class BiometricSensor extends NumericalSensor{
    private BiometricType type;

    public BiometricSensor(int code, int zoneCode, BiometricType type,
                           double minThreshold, double maxThreshold) throws ThresholdException {
        super(code, zoneCode, decideUnit(type), minThreshold, maxThreshold);
        this.type = type;
    }

    private static NumericalSensor.Unit decideUnit(BiometricType type) {
        switch (type) {
            case TEMPERATURE: return NumericalSensor.Unit.CELSIUS;
            case ACTIVITY:    return NumericalSensor.Unit.STEPS_PER_MIN;
            default:          return null;
        }
    }
    public BiometricType getType() { return type; }
    @Override
    public String getTypeAsString() {
        return type.toString();
    }

}