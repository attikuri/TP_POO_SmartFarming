import java.util.ArrayList;
import java.util.List;

public class CropZone extends GeographicalZone {
    private List<Crop> crops;


    public CropZone(String code, String name, ZoneStatus status, double surface) {
        super(code, name, status,surface);
        this.crops = new ArrayList<>();
    }

    public void addCrop(Crop crop) {

        this.crops.add(crop);
    }

    @Override
    public int getEntityCount() {
        return crops.size();
    }

    public void addSoilSensor(SoilSensor soilSensor) {
        if (soilSensor == null) throw new IllegalArgumentException("Sensor cannot be null.");
        this.sensors.add(soilSensor);
    }

    public void addEnvironmentalSensor(EnvironmentalSensor envSensor) {
        if (envSensor == null) throw new IllegalArgumentException("Sensor cannot be null.");
        this.sensors.add(envSensor);
    }
}