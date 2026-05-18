public class FeedingPgm {
    private String id;
    private String foodType;
    private double qttPerMeal;
    private int mealsPerDay;

    public String getFoodType(){
        return foodType;
    }
    public double getQttPerMeal(){
        return qttPerMeal;
    }
    public void setFoodType(String foodType){
        this.foodType = foodType;
    }
    public void setQuantity(double qtt){
        this.qttPerMeal = qtt;
    }
}
