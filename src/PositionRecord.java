public class PositionRecord {
    private Position position;
    private boolean outOfBoundary;

    public PositionRecord(Position position, boolean outOfBoundary) {
        this.position = position;
        this.outOfBoundary = outOfBoundary;
    }

    public Position getPosition() { return position; }
    public boolean isOutOfBoundary() { return outOfBoundary; }

}