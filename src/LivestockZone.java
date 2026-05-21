import java.util.ArrayList;
import java.util.List;

public class LivestockZone<T extends LiveStock> extends GeographicalZone {
    private List<T> animals;
    private FeedingPgm feedingProgram;

    public LivestockZone(String code, String name, ZoneStatus status, double surface, FeedingPgm feedingProgram) {
        super(code, name, status, surface);
        this.animals = new ArrayList<>();
        this.feedingProgram = feedingProgram;
    }

    public void addAnimal(T animal) {
        if (animal == null) {
            throw new IllegalArgumentException("Cannot add a null animal to the zone.");
        }
        this.animals.add(animal);
    }

    public void addBiometricSensor(BiometricSensor biometricSensor) {
        this.sensors.add(biometricSensor);
    }

     public void addGPSSensor(GPSCollar gpsCollar) {

        this.sensors.add(gpsCollar);
    }

     public FeedingPgm getFeedingProgram() {
        return feedingProgram;
    }

    public void setFeedingProgram(FeedingPgm feedingProgram) {
        this.feedingProgram = feedingProgram;
    }

    @Override
    public int getEntityCount() {
        return animals.size();
    }
}