import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class LiveStock {
    protected String id;
    protected String species;
    protected int age;
    protected double weight;
    protected HealthStatus healthState;
    protected double latitude;
    protected double longitude;
    private NumericalSensor biometricSensor;
    private GPSCollar gpsCollar;
    protected List<String> healthHistory;

    public LiveStock(String id, String species, int age, double weight, HealthStatus healthState) {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        if (weight <= 0) throw new IllegalArgumentException("Weight must be positive");
        if (id == "") throw new IllegalArgumentException("ID mustn't be empty");
        this.id = id;
        this.species = species;
        this.age = age;
        this.weight = weight;
        this.healthState = healthState;
        this.latitude = 0.0;   // updated by GPS sensor later?
        this.longitude = 0.0;
        this.healthHistory = new ArrayList<>();
        this.healthHistory.add(LocalDate.now() + " - Initial state: Weight=" + weight + "kg, Health=" + healthState);
    }

    public String getId(){
        return id;
    }
    public HealthStatus getHealthState(){
        return healthState;
    }
    public void setHealthState(HealthStatus newHealthState) {
        if (this.healthState != newHealthState) {
            this.healthHistory.add(LocalDate.now() + " - Health changed from " + this.healthState + " to " + newHealthState);
            this.healthState = newHealthState;
        }
    }

    public void updateWeight(double newWeight) throws IllegalArgumentException {
        if (newWeight <= 0) throw new IllegalArgumentException("Weight must be positive");
        this.healthHistory.add(LocalDate.now() + " - Weight updated from " + this.weight + "kg to " + newWeight + "kg");
        this.weight = newWeight;
    }

    public List<String> getHealthHistory() {
        return healthHistory;
    }
    public void updateGPSPosition(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    };

    public boolean checkGeofence(double minLat, double maxLat, double minLon, double maxLon) {
        return (this.latitude >= minLat && this.latitude <= maxLat &&
                this.longitude >= minLon && this.longitude <= maxLon);
    }

}

