package Types;

public class Direction {

    private String direction;
    private String směr;

    public Direction(String direction, String směr) {
        this.direction = direction;
        this.směr = směr;
    }

    public String getDirection() {
        return direction;
    }

    public String getSměr() {
        return směr;
    }

    @Override
    public String toString() {
        return "Direction: " + direction + ", Směr: " + směr;
    }
}
