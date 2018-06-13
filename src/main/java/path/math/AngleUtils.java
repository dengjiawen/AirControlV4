/**
 * Copyright 2018 (C) Jiawen Deng. All rights reserved.
 * <p>
 * This document is the property of Jiawen Deng.
 * It is considered confidential and proprietary.
 * <p>
 * This document may not be reproduced or transmitted in any form,
 * in whole or in part, without the express written permission of
 * Jiawen Deng.
 * <p>
 * -----------------------------------------------------------------------------
 * AngleUtils.java
 * -----------------------------------------------------------------------------
 * This is a class containing methods for performing angle related
 * calculations.
 * <p>
 * This class is a part of the CoreCalculation.
 * -----------------------------------------------------------------------------
 */

package main.java.path.math;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class AngleUtils {

    /**
     * Method for calculating the angle between two points. (using delta x and delta y as sides)
     * @param p1    Point 1
     * @param p2    Point 2
     * @return  double, angle
     */
    private static double getAngle(Point2D p1, Point2D p2) {

        /* use tangent to find acute angle */
        double acute_angle = Math.atan2(Math.abs(p1.getY() - p2.getY()), Math.abs(p1.getX() - p2.getX()));

        /* add multiples of 180 DEG to acute angle depending on target quadrant */
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

    /**
     * Method for calculating the angle between two points in a line.
     * @param line  target line
     * @return  double, angle
     */
    public static double getAngle(Line2D line) {

        /* call on overloaded method */
        return getAngle(line.getP1(), line.getP2());

    }

}
