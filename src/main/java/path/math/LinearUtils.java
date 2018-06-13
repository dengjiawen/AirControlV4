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
 * LinearUtils.java
 * -----------------------------------------------------------------------------
 * This is a class containing methods for performing calculations on linear
 * functions.
 * <p>
 * This class is a part of the CoreCalculation.
 * -----------------------------------------------------------------------------
 */

package main.java.path.math;

import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.path.Intersection;
import main.java.path.Paths;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LinearUtils {

    /**
     * Static block for initializing constants for LinearUtils class.
     */
    static {

        LogUtils.printGeneralMessage("Initializing LinearUtils...");
        point_intervals = Constants.getInt("pointIntervals", Definitions.ZERO_DAY_PATCH_SUPPLEMENTARY);

    }

    public static int point_intervals;  // defines spacing between jump nodes and intersections.

    /**
     * Method that returns the slope of a line.
     * @param map_path  the target line
     * @return  double, slope
     */
    private static double getSlope(Line2D map_path) {

        /* using delta y/delta x formula */
        return (map_path.getY1() - map_path.getY2()) / (map_path.getX1() - map_path.getX2());

    }

    /**
     * Method that returns the intercept of a linear line.
     * @param m variable coefficient
     * @param x x value
     * @param y y value
     * @return  double, intercept of linear function
     */
    private static double getIntercept(double m, double x, double y) {

        return y - m * x;

    }

    /**
     * Method that gets a point on the line/function defined by m, k at x.
     * @param m variable coefficient
     * @param k x value
     * @param x y value
     * @return  Point2D, point on the line
     */
    private static Point2D getPoint(double m, double k, double x) {

        return new Point2D.Double(x, m * x + k);

    }

    /**
     * Method that gets a jump point around an intersection.
     * @param intersection  target intersection
     * @param origin        target line
     * @param orientation   node before/after intersection (enum)
     * @return  Point2D, jump node around an intersection
     */
    public static Point2D getJumpPoint(Intersection intersection, Paths origin, Orientation orientation) {

        /* get slope and intercept for the linear function */
        double m = LinearUtils.getSlope(origin.getPath());
        double k = LinearUtils.getIntercept(m, origin.getPath().getX1(), origin.getPath().getY1());

        double x = 0;

        /* calculate x value */
        switch (orientation) {
            case PREVIOUS:
                x = intersection.getX() + Math.sqrt(Math.pow(point_intervals, 2) / (1 + Math.pow(m, 2)));
                break;
            case NEXT:
                x = intersection.getX() - Math.sqrt(Math.pow(point_intervals, 2) / (1 + Math.pow(m, 2)));
                break;
        }

        /* find jump point */
        Point2D jump_point = getPoint(m, k, x);

        LogUtils.printGeneralMessage("LinearUtils calculation, jump point " + jump_point + " found for intersection " + intersection + " with orientation " + orientation + ".");

        /* check of point is online; if it is not, return a junk point */
        if (xIsOnLine(jump_point.getX(), origin.getPath()) && yIsOnLine(jump_point.getY(), origin.getPath())) {
            return jump_point;
        } else {
            LogUtils.printGeneralMessage("Jump point " + jump_point + " is junk. Ditching.");
            return new Point2D.Double(0, 0);
        }

    }

    /**
     * Method that finds an intersection between two lines.
     * @param p1    line 1
     * @param p2    line 2
     * @return  Intersection object (if exists)
     */
    public static Intersection findIntersect(Paths p1, Paths p2) {

        /* convert path into line */
        Line2D l1 = p1.getPath();
        Line2D l2 = p2.getPath();

        /* retrieve the 4 points */
        Point2D A = l1.getP1();
        Point2D B = l1.getP2();

        Point2D C = l2.getP1();
        Point2D D = l2.getP2();

        /* use linear function to figure out intersecting x values */
        double a1 = B.getY() - A.getY();
        double b1 = A.getX() - B.getX();
        double c1 = a1 * (A.getX()) + b1 * (A.getY());

        double a2 = D.getY() - C.getY();
        double b2 = C.getX() - D.getX();
        double c2 = a2 * (C.getX()) + b2 * (C.getY());

        double determinant = a1 * b2 - a2 * b1;

        if (determinant == 0) {
            return null;
        } else {
            double x = (b2 * c1 - b1 * c2) / determinant;
            double y = (a1 * c2 - a2 * c1) / determinant;

            /* check if point is on line */
            if (xIsOnLine(x, l1) && xIsOnLine(x, l2) && yIsOnLine(y, l1) && yIsOnLine(y, l2)) {
                LogUtils.printGeneralMessage("LinearUtils calculation, new intersection found for path " + p1 + " and path " + p2 + ".");
                return new Intersection(new Point2D.Double(x, y), new Paths[]{p1, p2});
            }
        }

        LogUtils.printGeneralMessage("LinearUtils calculation, intersection found for path " + p1 + " and path " + p2 + " was junk. Ditching.");
        return null;

    }

    /**
     * Method that determines if the x component of a point is on a line.
     * @param x
     * @param line
     * @return  boolean, whether x is on line
     */
    private static boolean xIsOnLine(double x, Line2D line) {

        /* check if x is between the two points on the line */
        if (x >= line.getX1()) {

            return x <= line.getX2();

        } else {

            return x >= line.getX2();

        }

    }

    /**
     * Method that determines if the y component of a point is on a line.
     * @param y
     * @param line
     * @return  boolean, whether y is on line
     */
    private static boolean yIsOnLine(double y, Line2D line) {

        /* check if y is between the two points on the line */
        if (y >= line.getY1()) {

            return y <= line.getY2();

        } else {

            return y >= line.getY2();

        }

    }

    /**
     * Enumerated type that dictates the jump point orientation.
     */
    public enum Orientation {
        PREVIOUS, NEXT
    }

    /**
     * Standard merge sort method
     * @param unsorted_array
     * @return  sorted array
     */
    public static ArrayList<Double> mergeSort(ArrayList<Double> unsorted_array) {

        if (unsorted_array.size() == 1) {
            return unsorted_array;
        }

        int middle = unsorted_array.size() / 2;

        ArrayList<Double> left_arr = new ArrayList<>();
        ArrayList<Double> right_arr = new ArrayList<>();

        for (int i = 0; i < middle; i++) {
            left_arr.add(unsorted_array.get(i));
        }

        for (int i = middle; i < unsorted_array.size(); i++) {
            right_arr.add(null);
        }

        for (int i = middle; i < unsorted_array.size(); i++) {
            right_arr.set(i - middle, unsorted_array.get(i));
        }

        return merge(mergeSort(left_arr), mergeSort(right_arr));

    }

    /**
     * Helper merge method for the merge sort method.
     * @param arr1  arraylist 1
     * @param arr2  arraylist 2
     * @return  merged array
     */
    private static ArrayList<Double> merge(ArrayList<Double> arr1, ArrayList<Double> arr2) {

        double[] merged_array = new double[arr1.size() + arr2.size()];

        int i1, i2, im;
        i1 = 0;
        i2 = 0;
        im = 0;

        while (im != merged_array.length) {

            if (i1 == arr1.size()) {
                merged_array[im++] = arr2.get(i2++);
            } else if (i2 == arr2.size()) {
                merged_array[im++] = arr1.get(i1++);
            } else if (arr1.get(i1) <= arr2.get(i2)) {
                merged_array[im++] = arr1.get(i1++);
            } else if (arr2.get(i2) <= arr1.get(i1)) {
                merged_array[im++] = arr2.get(i2++);
            }

        }

        return new ArrayList<Double>() {{
            for (int i = 0; i < merged_array.length; i++) {
                add(merged_array[i]);
            }
        }};

    }

}
