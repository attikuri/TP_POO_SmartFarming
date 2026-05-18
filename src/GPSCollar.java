import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class GPSCollar extends Sensor{
    // boundary of the zone: [minLat, maxLat, minLon, maxLon]
    private double minLat, maxLat, minLon, maxLon;

    private PositionRecord[] record;
    private HashMap<Date, PositionRecord[]> history;

    // ==================== CONSTRUCTOR ====================
    public GPSCollar(int code, int zoneCode,
                     double minLat, double maxLat,
                     double minLon, double maxLon) {
        super(code, zoneCode);
        this.minLat = minLat;
        this.maxLat = maxLat;
        this.minLon = minLon;
        this.maxLon = maxLon;
        this.record = new PositionRecord[24];
        this.history = new HashMap<>();
    }

    //  METHODS
    public void addPosition(int hour, Position p) {
        if (hour < 0 || hour > 23) return;
        boolean outOfBoundary = isOutOfBoundary(p);
        record[hour] = new PositionRecord(p, outOfBoundary);
    }

    public boolean isOutOfBoundary(Position p) {
        return p.getLatitude()  < minLat || p.getLatitude()  > maxLat ||
                p.getLongitude() < minLon || p.getLongitude() > maxLon;
    }




    public void saveDailyRecord(Date date) {
        for (int i = 0; i < 24; i++) {
            if (record[i] == null) {
                System.out.println("Day not complete, hour " + i + " missing.");
                return;
            }
        }
        PositionRecord[] copy = new PositionRecord[24];
        System.arraycopy(record, 0, copy, 0, 24);
        history.put(date, copy);
        record = new PositionRecord[24];
        System.out.println("GPS day saved successfully!");
    }

    public PositionRecord[] getRecord() { return record; }
    public HashMap<Date, PositionRecord[]> getHistory() { return history; }

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

        for (Map.Entry<Date, PositionRecord[]> entry : history.entrySet()) {
            Date date = entry.getKey();

            if (!date.before(startDate) && !date.after(endDate)) {
                foundAny = true;
                System.out.println("==========================================");
                System.out.println("Date: " + date);
                System.out.println("==========================================");

                PositionRecord[] dayRecord = entry.getValue();
                for (int hour = 0; hour < 24; hour++) {
                    if (dayRecord[hour] != null) {
                        System.out.println("  Hour " + String.format("%02d", hour) + ":00"
                                + " | Lat: " + dayRecord[hour].getPosition().getLatitude()
                                + " Lon: " + dayRecord[hour].getPosition().getLongitude()
                                + " | Out of boundary: " + dayRecord[hour].isOutOfBoundary());
                    } else {
                        System.out.println("  Hour " + String.format("%02d", hour) + ":00"
                                + " | No position recorded.");
                    }
                }
            }
        }

        if (!foundAny) {
            System.out.println("No records found between " + startDate + " and " + endDate);
        }
    }

}