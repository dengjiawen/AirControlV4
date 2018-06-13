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
 * MapUtils.java
 * -----------------------------------------------------------------------------
 * This is a class containing Line2D references to all of the Paths.
 * It also keeps a reference to all of the Intersections.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.path;

import main.java.constants.Definitions;
import main.java.constants.ParseUtils;
import main.java.path.math.LinearUtils;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class MapUtils {

    /**
     * Reference of all Line2D objects for the Paths.
     */
    static Line2D rwy26 = loadLine("rwy26");
    static Line2D rwy35 = loadLine("rwy35");
    static Line2D rwy3L = loadLine("rwy3L");
    static Line2D rwy3R = loadLine("rwy3R");

    static Line2D taxiE = loadLine("taxiE");
    static Line2D taxiD = loadLine("taxiD");

    static Line2D taxiF = loadLine("taxiF");
    static Line2D taxiF1 = loadLine("taxiF1");

    static Line2D taxiC1 = loadLine("taxiC1");
    static Line2D taxiC2 = loadLine("taxiC2");
    static Line2D taxiC3 = loadLine("taxiC3");

    static Line2D taxiQ = loadLine("taxiQ");
    static Line2D taxiH = loadLine("taxiH");
    static Line2D taxiP = loadLine("taxiP");

    static Line2D taxiB = loadLine("taxiB");

    static Line2D taxiA = loadLine("taxiA");

    /**
     * Reference of all intersecting points.
     */
    static ArrayList<Intersection> intersections = new ArrayList<>();

    /**
     * Method that initializes the MapUtils class.
     */
    public static void init() {

        /* for every path, find all intersections with other paths */
        for (Paths a : Paths.values()) {
            for (Paths b : Paths.values()) {

                Intersection intersection = LinearUtils.findIntersect(a, b);
                if (intersection != null) {

                    int index_of_existing_intersection = 0;
                    boolean intersection_exists = false;

                    /* checks if intersection already exists */
                    for (Intersection i : intersections) {
                        if (i.equals(intersection)) {
                            intersection_exists = true;
                            index_of_existing_intersection = intersections.indexOf(i);
                        }
                    }

                    /* if intersection do not exist, add to list of intersections
                     * and add the two paths to the intersection for reference */
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

        /* for every path, sort intersection arrays in order */
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

        /* update all nodes in the intersection */
        intersections.forEach(e -> e.updateNode());

    }

    /**
     * Method that sorts the intersection array for each path using selection sort.
     * It sorts by distance from the first point on the Path (low -> high).
     * @param intersections intersection array
     * @param path  the target path
     * @return  sorted array
     */
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

    private static Line2D loadLine(String line_name) {

        double x1 = ParseUtils.parseDouble(Definitions.DLC_1.getPath(), line_name + "X1");
        double x2 = ParseUtils.parseDouble(Definitions.DLC_1.getPath(), line_name + "X2");

        double y1 = ParseUtils.parseDouble(Definitions.DLC_1.getPath(), line_name + "Y1");
        double y2 = ParseUtils.parseDouble(Definitions.DLC_1.getPath(), line_name + "Y2");

        return new Line2D.Double(x1, y1, x2, y2);

    }

}
