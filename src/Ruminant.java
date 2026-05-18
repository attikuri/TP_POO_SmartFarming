public class Ruminant extends LiveStock {
    private double milkProduction;

    public Ruminant(String id, String species, int age, double weight, HealthStatus healthState){
        super(id, species, age, weight, healthState);
        this.milkProduction = 0;

    }
    public double getMilkProduction(){
        return milkProduction;
    };

}
