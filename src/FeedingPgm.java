public class FeedingPgm {
    private String id;
    private String foodType;
    private double qttPerMeal;
    private int mealsPerDay;

    public FeedingPgm(String id, String foodType, double qttPerMeal, int mealsPerDay) {
        if (qttPerMeal <= 0 || mealsPerDay <= 0) {
            throw new IllegalArgumentException("Value entered must be strictly positive.");
        }
        if (foodType == null || foodType.trim().isEmpty()) {
            throw new IllegalArgumentException("type must be specified.");
        }
        this.id = id;
        this.foodType = foodType;
        this.qttPerMeal = qttPerMeal;
        this.mealsPerDay = mealsPerDay;
    }


    public double calculateDailyRequirement() {
        return qttPerMeal * mealsPerDay;
    }

    public String getFoodType() { return foodType; }
    public double getQttPerMeal() { return qttPerMeal; }

     public void setQuantity(double qtt) {
        if (qtt <= 0) throw new IllegalArgumentException("Invalid Quantity.");
        this.qttPerMeal = qtt;
    }

    @Override
    public String toString() {
        return String.format("Program [%s]: Food: %s | %.2f kg/meal | %d times/day | Total: %.2f kg/day",
                id, foodType, qttPerMeal, mealsPerDay, calculateDailyRequirement());
    }
}