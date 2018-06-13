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
 * This is a class containing methods for performing circle and arc related
 * calculations. This class is mostly used for turn events.
 * <p>
 * This class is a part of the CoreCalculation.
 * -----------------------------------------------------------------------------
 */

package main.java.path.math;

import main.java.path.Intersection;
import main.java.path.Paths;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class LinearUtils {

    public final static int point_intervals = 100;

    public static double getSlope(Line2D map_path) {

        return (map_path.getY1() - map_path.getY2()) / (map_path.getX1() - map_path.getX2());

    }

    public static double getIntercept(double m, double x, double y) {

        return y - m * x;

    }

    public static Point2D getPoint(double m, double k, double x) {

        return new Point2D.Double(x, m * x + k);

    }

    public static Point2D getJumpPoint(Intersection intersection, Paths origin, Orientation orientation) {

        double m = LinearUtils.getSlope(origin.getPath());
        double k = LinearUtils.getIntercept(m, origin.getPath().getX1(), origin.getPath().getY1());

        double x = 0;

        switch (orientation) {
            case PREVIOUS:
                x = intersection.getX() + Math.sqrt(Math.pow(point_intervals, 2) / (1 + Math.pow(m, 2)));
                break;
            case NEXT:
                x = intersection.getX() - Math.sqrt(Math.pow(point_intervals, 2) / (1 + Math.pow(m, 2)));
                break;
        }

        Point2D jump_point = getPoint(m, k, x);
        if (xIsOnLine(jump_point.getX(), origin.getPath()) && yIsOnLine(jump_point.getY(), origin.getPath())) {
            return jump_point;
        } else return new Point2D.Double(0, 0);

    }

    public static Intersection findIntersect(Paths p1, Paths p2) {

        Line2D l1 = p1.getPath();
        Line2D l2 = p2.getPath();

        Point2D A = l1.getP1();
        Point2D B = l1.getP2();

        Point2D C = l2.getP1();
        Point2D D = l2.getP2();

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

            if (xIsOnLine(x, l1) && xIsOnLine(x, l2) && yIsOnLine(y, l1) && yIsOnLine(y, l2)) {
                return new Intersection(new Point2D.Double(x, y), new Paths[]{p1, p2});
            }
        }

        return null;

    }

    public static boolean xIsOnLine(double x, Line2D line) {

        if (x >= line.getX1()) {

            return x <= line.getX2();

        } else {

            return x >= line.getX2();

        }

    }

    public static boolean yIsOnLine(double y, Line2D line) {

        if (y >= line.getY1()) {

            return y <= line.getY2();

        } else {

            return y >= line.getY2();

        }

    }


    public enum Orientation {
        PREVIOUS, NEXT
    }

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

    public static ArrayList<Double> merge(ArrayList<Double> arr1, ArrayList<Double> arr2) {

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
