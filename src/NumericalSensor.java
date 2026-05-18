import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class NumericalSensor extends Sensor {
    public enum Unit {
        CELSIUS, PERCENTAGE, MM, PH, MG_PER_L, STEPS_PER_MIN, MG_PER_KG
    }
    // attributes
    private Unit unit;
    private Reading[] record;       // 24 slots, one per hour
    private double minThreshold;
    private double maxThreshold;
    private char period;           // 'h' for hourly
    private HashMap<Date, Reading[]> history;  // key: date, value: 24h record

    // Methods
    public NumericalSensor(int  code, int  zoneCode, Unit unit,
                           double minThreshold, double maxThreshold) throws ThresholdException {
        super(code, zoneCode);
        this.unit = unit;
        if(minThreshold >= maxThreshold ){
            throw new ThresholdException();
        } else {
            this.minThreshold = minThreshold;
            this.maxThreshold = maxThreshold;
        }

        this.record = new Reading[24];
        this.period = 'h';
        this.history = new HashMap<>();
    }
    // add reading each hour
    public void addReading(int hour, double value) {
        if (hour < 0 || hour > 23) return;
        boolean outOfRange = isOutOfRange(value);
        record[hour] = new Reading(value, outOfRange);
    }
    // check if the recorded value at determined hour
    public boolean isOutOfRange(double value) {
        return value < minThreshold || value > maxThreshold;
    }

    // save the day redord in the history
    public void saveDailyRecord(Date date) {
        // check if all 24 slots are filled
        for (int i = 0; i < 24; i++) {
            if (record[i] == null) {
                System.out.println("Day not complete yet, hour " + i + " is missing.");
                return; // don't save, day is not finished
            }
        }
        // all 24 readings are present → save and reset
        Reading[] copy = new Reading[24];
        System.arraycopy(record, 0, copy, 0, 24);
        history.put(date, copy);
        record = new Reading[24]; // reset for new day
        System.out.println("Day saved successfully!");
    }
    // ==================== GETTERS & SETTERS ====================
    public Unit getUnit() { return unit; }

    public Reading[] getRecord() { return record; }

    public double getMinThreshold() { return minThreshold; }

    public double getMaxThreshold() { return maxThreshold; }

    public char getPeriod() { return period; }

    public HashMap<Date,Reading[]> getHistory() { return history; }

    public void setMinThreshold(double min) { this.minThreshold = min; }

    public void setMaxThreshold(double max) { this.maxThreshold = max; }

    @Override
    public void browseHistory(Date startDate, Date endDate) throws IllegalArgumentException {

        // ========== EXCEPTIONS ==========
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }

        // ========== BROWSE ==========
        boolean foundAny = false;

        for (Map.Entry<Date, Reading[]> entry : history.entrySet()) {
            Date date = entry.getKey();

            // check if date is within range (inclusive)
            if (!date.before(startDate) && !date.after(endDate)) {
                foundAny = true;
                System.out.println("==========================================");
                System.out.println("Date: " + date);
                System.out.println("==========================================");

                Reading[] dayRecord = entry.getValue();
                for (int hour = 0; hour < 24; hour++) {
                    if (dayRecord[hour] != null) {
                        System.out.println("  Hour " + String.format("%02d", hour) + ":00"
                                + " | Value: " + dayRecord[hour].getValue()
                                + " " + getUnit()
                                + " | Out of range: " + dayRecord[hour].isOutOfRange());
                    } else {
                        System.out.println("  Hour " + String.format("%02d", hour) + ":00"
                                + " | No reading recorded.");
                    }
                }
            }
        }

        if (!foundAny) {
            System.out.println("No records found between " + startDate + " and " + endDate);
        }
    }
    // in NumericalSensor
    public abstract String getTypeAsString();
    public SensorCardGUI getSensorCard() {
        return new SensorCardGUI(this); // works for all subclasses
    }
}
