import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class TheShapeFixer {
    public boolean isValid(Shape2D shape) {
        List<Point> points = shape.getPoints();

        if (points.size() < 4 || !points.getFirst().equals(points.getLast())) {
            return false;
        }

        return IntStream.range(0, points.size() - 1)
                .boxed()
                .flatMap(i -> IntStream.range(i + 2, points.size() - 1)
                        .filter(j -> !(i == 0 && j == points.size() - 2))
                        .mapToObj(j -> new Point[]{points.get(i), points.get(i + 1), points.get(j), points.get(j + 1)})
                )
                .noneMatch(segment -> segmentsIntersect(segment[0], segment[1], segment[2], segment[3]));
    }

    public Shape2D repair(Shape2D shape) {
        List<Point> points = shape.getPoints();
        if (isValid(shape)) {
            return shape;
        }

        var fixedPoints = new ArrayList<>(new LinkedHashSet<>(points));
        var convexHull = buildConvexHull(fixedPoints);

        return new Shape2D(convexHull);
    }

    private boolean segmentsIntersect(Point p1, Point q1, Point p2, Point q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        return Stream.of(
                o1 == 0 && onSegment(p1, q1, p2),
                o2 == 0 && onSegment(p1, q1, q2),
                o3 == 0 && onSegment(p2, q2, p1),
                o4 == 0 && onSegment(p2, q2, q1)
        ).anyMatch(Boolean::booleanValue);
    }

    private int orientation(Point p, Point q, Point r) {
        int val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);

        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }

    private boolean onSegment(Point p, Point q, Point r) {
        return r.x <= Math.max(p.x, q.x) && r.x >= Math.min(p.x, q.x) &&
                r.y <= Math.max(p.y, q.y) && r.y >= Math.min(p.y, q.y);
    }

    private List<Point> buildConvexHull(List<Point> points) {
        int n = points.size();
        if (n < 3) return points;

        List<Point> hull = new ArrayList<>();
        int leftMost = 0;
        for (int i = 1; i < n; i++) {
            if (points.get(i).x < points.get(leftMost).x) {
                leftMost = i;
            }
        }

        int p = leftMost, q;

        do {
            hull.add(points.get(p));

            q = (p + 1) % n;
            for (int i = 0; i < n; i++) {
                if (orientation(points.get(p), points.get(i), points.get(q)) == 2) {
                    q = i;
                }
            }

            p = q;
        } while (p != leftMost);

        return hull;
    }

    public static void main(String[] args) {
        List<Point> points = new LinkedList<>();
        points.add(new Point(0, 0));
        points.add(new Point(3, 0));
        points.add(new Point(3, 4));
        points.add(new Point(0, 4));
        points.add(new Point(0, 1));

        Shape2D shape = new Shape2D(points);
        TheShapeFixer fixer = new TheShapeFixer();

        if(fixer.isValid(shape)){
            System.out.println("Is valid: true");
            return;
        }

        System.out.println("Is valid: false!");
        Shape2D repairedShape = fixer.repair(shape);
        System.out.println("Repaired shape: " + repairedShape.getPoints());
    }
}
