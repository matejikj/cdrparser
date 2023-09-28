package Types;

public class Area {
    private String area;
    private String oblast;

    public Area(String area, String oblast) {
        this.area = area;
        this.oblast = oblast;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOblast() {
        return oblast;
    }

    public void setOblast(String oblast) {
        this.oblast = oblast;
    }

    @Override
    public String toString() {
        return "Area: " + area + ", Oblast: " + oblast;
    }
}
