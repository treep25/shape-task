import java.util.List;

public class Shape2D {
    List<Point> points;

    public Shape2D(List<Point> points) {
        this.points = points;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }
}
