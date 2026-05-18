import java.util.ArrayList;
import java.util.List;

public abstract class GeographicalZone {
    protected String code;
    protected String name;
    protected ZoneStatus status;
    protected List<Sensor> sensors;
    protected List<ProductionRecord> productionHistory;

    public GeographicalZone(String code, String name, ZoneStatus status){
        this.code = code;
        this.name = name;
        this.status = status;
        this.sensors = new ArrayList<>();
        this.productionHistory = new ArrayList<>();
    }
    public GeographicalZone(){};

    public void addProductionRecord(ProductionRecord record){
        this.productionHistory.add(record);
    }

    public List<ProductionRecord> getProductionHistory() {
        return this.productionHistory;
    }

    public String getCode(){
        return this.code;
    };
    public String getName(){
        return this.name;
    };
    public ZoneStatus getStatus(){
        return this.status;
    }
    public void suspend(){
        this.status = ZoneStatus.SUSPENDED;
        for (Sensor s : sensors){
            if(s.getStatus() != Sensor.Status.FAULTY){
            s.suspend(); }
        }
    };
    public void reactivate(){
        this.status = ZoneStatus.ACTIVE;
        for (Sensor s : sensors){
            if(s.getStatus() != Sensor.Status.FAULTY){
                s.activate(); }
        }
    };
    public abstract int getEntityCount();


}
