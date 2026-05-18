public class SoilSensor extends NumericalSensor{
    private SoilType type;

    public SoilSensor(int  code, int  zoneCode, SoilType type,
                      double minThreshold, double maxThreshold) throws ThresholdException {
        super(code, zoneCode, decideUnit(type), minThreshold, maxThreshold);
        this.type = type;
    }

    private static NumericalSensor.Unit decideUnit(SoilType type) {
        switch (type) {
            case SOIL_MOISTURE: return NumericalSensor.Unit.PERCENTAGE;
            case PH:            return NumericalSensor.Unit.PH;
            case NITROGEN:      return NumericalSensor.Unit.MG_PER_KG;
            default:            return null;
        }
    }
    public SoilType getType() { return type; }
    @Override
    public String getTypeAsString() {
        return type.toString(); // returns "PH", "NITROGEN" etc.
    }

}