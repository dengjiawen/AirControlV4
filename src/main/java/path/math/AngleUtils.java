package main.java.path.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class AngleUtils {

    public static double getAngle(Point2D p1, Point2D p2) {

        double acute_angle = Math.atan2(Math.abs(p1.getY() - p2.getY()), Math.abs(p1.getX() - p2.getX()));

        if (p1.getX() < p2.getX()) {
            if (p1.getY() < p2.getY()) {
                return Math.PI / 2 + acute_angle;
            } else {
                return Math.PI / 2 - acute_angle;
            }
        } else {
            if (p1.getY() < p2.getY()) {
                return (-1) * (Math.PI / 2 + acute_angle);
            } else {
                return (-1) * (Math.PI / 2 - acute_angle);
            }
        }

    }

    public static double getAngle(Line2D line) {

        return getAngle(line.getP1(), line.getP2());

    }

}
