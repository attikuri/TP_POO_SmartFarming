import java.util.Date;
public abstract  class Sensor {
    // Status enum
    public enum Status {
        ACTIVE, FAULTY, SUSPENDED
    }
    private Status status;
    private int code ;
    private int zoneCode ;
    //constrictor
    public Sensor(int code, int zoneCode) {
        this.code = code;
        this.zoneCode = zoneCode;
        this.status = Status.ACTIVE; // default when created
    }
    //public abstract void sendReading();

    public void suspend() {
        this.status = Status.SUSPENDED;
    }

    public void activate() {
        this.status = Status.ACTIVE;
    }

    public void markFaulty() {
        this.status = Status.FAULTY;
    }

    // ==================== GETTERS & SETTERS ====================
    public int  getCode() {
        return code;
    }

    public int getZoneCode() {
        return zoneCode;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public abstract void browseHistory(Date startDate, Date endDate) throws IllegalArgumentException;
}
