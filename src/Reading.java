public class Reading {
    private double value;
    private boolean outOfRange;  // result of threshold check

    public Reading(double value, boolean outOfRange) {
        this.value = value;
        this.outOfRange = outOfRange;
    }

    public double getValue() { return value; }
    public boolean isOutOfRange() { return outOfRange; }
}