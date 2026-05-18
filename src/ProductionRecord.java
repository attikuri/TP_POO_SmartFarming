import java.time.LocalDate;

public class ProductionRecord {
    private String zoneId;
    private LocalDate date;
    private double productionVal;
    private String unit;

    public double getProductionValue(){
        return productionVal;
    }
    public String getUnit(){
        return unit;
    }
    public LocalDate getDate(){
        return date;
    }
}
