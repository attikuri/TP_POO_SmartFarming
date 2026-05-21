
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alert {
    private String id;
    private int sensorCode;
    private double readingValue;
    private AlertLevel level;
    private LocalDateTime date;
    private boolean isAcknowledged;

    public Alert(String id, int sensorCode, double readingValue, AlertLevel level) {
        this.id = id;
        this.sensorCode = sensorCode;
        this.readingValue = readingValue;
        this.level = level;
        this.date = LocalDateTime.now();
        this.isAcknowledged = false;
    }

    public String getId() { return id; }
    public AlertLevel getLevel() { return level; }
    public boolean getIsAcknowledged() { return isAcknowledged; }
    public int getSensorCode() { return sensorCode; }
    public double getReadingValue() { return readingValue; }

    public void acknowledge() {
        this.isAcknowledged = true; // ie alert was handled
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedTime = this.date.format(formatter);

        return String.format("[%s] %s | Time: %s | Sensor: %d | Value: %.2f | Handled: %b",
                this.level,
                this.id,
                formattedTime,
                this.sensorCode,
                this.readingValue,
                this.isAcknowledged);
    }
}
