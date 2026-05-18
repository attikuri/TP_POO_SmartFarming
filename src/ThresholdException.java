
public class ThresholdException extends Exception {
    public ThresholdException() {
        super("the max value can't be less then the min value of the the threshold of sensor ");
    }
}