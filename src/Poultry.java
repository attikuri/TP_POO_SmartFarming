import java.util.List;

public class Poultry extends LiveStock {
    private int eggProduction;

    public Poultry(String id, String species, int age, double weight, HealthStatus healthState){
        super(id,species, age, weight, healthState);
        this.eggProduction = 0;
    }
    public int getEggProduction(){
        return eggProduction;
    }
}
