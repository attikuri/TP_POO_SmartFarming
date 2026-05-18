import java.time.LocalDate;

public class Crop {
    private String id;
    private double maxPhRange;
    private double minPhRange;
    private double minHumidityRange;
    private double maxHumidityRange;
    private LocalDate plantingDate;
    private LocalDate harvestDate;
    private GrowthStatus growthStage;
    private CropFamily crop;

    public Crop(String id, double maxPhRange, double minPhRange, double minHumidityRange, double maxHumidityRange, LocalDate plantingDate, LocalDate harvestDate, GrowthStatus growthStage, CropFamily crop){

        this.id = id;
        this.maxPhRange = maxPhRange;
        this.minPhRange = minPhRange;
        this.maxHumidityRange = maxHumidityRange;
        this.minHumidityRange = minHumidityRange;
        this.plantingDate = plantingDate;
        this.harvestDate = harvestDate;
        this.growthStage = growthStage;
        this.crop = crop;
    }

    public Crop(String id, double maxPhRange, double minPhRange, double minHumidityRange, double maxHumidityRange, LocalDate of, LocalDate of1, GrowthStatus growthStatus) {

    }

    public String getId(){
        return id;
    }
    public GrowthStatus getGrowthStage(){
        return growthStage;
    }
    public double[] getPhRange(){
        return new double[]{minPhRange, maxPhRange};
    }
    public double[] getHumidityRange(){
        return new double[]{minHumidityRange, maxHumidityRange};
    }
    public boolean isHarvestReady(){
        return (growthStage == GrowthStatus.RECOLTE);
    }

}
