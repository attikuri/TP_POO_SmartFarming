public class Aquaculture {
    public String id;
    public String species;
    public int animalCount;
    public double waterTemp;
    public double dissolvedO2;
    public double ph;

    public Aquaculture(String id, String species, int animalCount, double waterTemp, double dissolvedO2, double ph){
        this.id = id;
        this.species = species;
        this.animalCount = animalCount;
        this.waterTemp = waterTemp;
        this.dissolvedO2 = dissolvedO2;
        this.ph = ph;
    }

    public String getId(){
        return id;
    }
    public String getSpecies(){
        return species;
    }
    public int getAnimalCount(){
        return animalCount;
    }
    public void updateWaterParams(double temp, double oxygen, double ph){}
   // public FeedingPgm getFeedingPgm(){}
}
