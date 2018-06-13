package main.java.path;

import main.java.path.math.LinearUtils;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class MapUtils {

    static Line2D rwy26 = new Line2D.Double(9369, 576, 5649, 594);
    static Line2D rwy35 = new Line2D.Double(6033, 3487, 6017, 145);
    static Line2D rwy3L = new Line2D.Double(1719, 6222, 7105, 768);
    static Line2D rwy3R = new Line2D.Double(4360, 4032, 8265, 71);

    static Line2D taxiE = new Line2D.Double(2433, 6358, 7177, 1558);
    static Line2D taxiD = new Line2D.Double(7194, 1625, 6707, 1142);

    static Line2D taxiF = new Line2D.Float(4729, 3107, 6357, 3114);
    static Line2D taxiF1 = new Line2D.Float(6182, 3167, 7140, 2622);

    static Line2D taxiC1 = new Line2D.Float(7600, 1220, 8831, 1221);
    static Line2D taxiC2 = new Line2D.Float(6917, 1990, 7735, 1175);
    static Line2D taxiC3 = new Line2D.Float(7077, 2926, 7076, 1740);

    static Line2D taxiQ = new Line2D.Float(2052, 5846, 2555, 6274);
    static Line2D taxiH = new Line2D.Float(3521, 4362, 4006, 4810);
    static Line2D taxiP = new Line2D.Float(4933, 3912, 4664, 3646);

    static Line2D taxiB = new Line2D.Float(8016, 540, 8017, 1271);

    static Line2D taxiA = new Line2D.Float(8792, 1313, 8793, 548);

    static ArrayList<Intersection> intersections = new ArrayList<>();

    public static void init() {

        for (Paths a : Paths.values()) {
            for (Paths b : Paths.values()) {

                Intersection intersection = LinearUtils.findIntersect(a, b);
                if (intersection != null) {
                    int index_of_existing_intersection = 0;
                    boolean intersection_exists = false;
                    for (Intersection i : intersections) {
                        if (i.equals(intersection)) {
                            intersection_exists = true;
                            index_of_existing_intersection = intersections.indexOf(i);
                        }
                    }

                    if (!intersection_exists) intersections.add(intersection);
                    else {
                        if (!intersection.intersects(a)) {
                            intersections.get(index_of_existing_intersection).addPath(a);
                        } else if (!intersection.intersects(b)) {
                            intersections.get(index_of_existing_intersection).addPath(b);
                        }
                    }
                }

            }
        }

        for (Paths path : Paths.values()) {

            ArrayList<Intersection> path_intersects = new ArrayList<>();

            for (Intersection i : intersections) {
                if (i.intersects(path)) {
                    path_intersects.add(i);
                }
            }

            path.setIntersections(
                    sortIntersectArray(path_intersects.toArray(new Intersection[path_intersects.size()]), path));

        }

        intersections.forEach(e -> {
            e.updateNode();
        });

    }

    static Intersection[] sortIntersectArray(Intersection[] intersections, Paths path) {

        for (int i = 0; i < intersections.length - 1; i++) {

            int index = i;

            for (int j = i + i; j < intersections.length; j++) {
                if (intersections[j].distance(path.getPath().getP1()) < intersections[index].distance(path.getPath().getP1())) {
                    index = j;
                }
            }

            if (index != i) {
                Intersection temporary_node = intersections[i];
                intersections[i] = intersections[index];
                intersections[index] = temporary_node;
            }

        }

        return intersections;

    }

}
