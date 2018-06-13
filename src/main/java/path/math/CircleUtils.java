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
 * CircleUtils.java
 * -----------------------------------------------------------------------------
 * This is a class containing methods for performing circle and arc related
 * calculations. This class is mostly used for turn events.
 * <p>
 * This class is a part of the CoreCalculation.
 * -----------------------------------------------------------------------------
 */

package main.java.path.math;

import main.java.common.LogUtils;
import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;

import java.awt.geom.*;
import java.util.ArrayList;

public class CircleUtils {

    public static Arc2D active_arc;                         // for DEBUG purposes only; shows the latest arc
    public static ArrayList<TurningNodes> node_collection;  // for DEBUG purposes only; shows the latest arc nodes

    /**
     * A special type of temporary node that only exists during a turn.
     */
    public static class TurningNodes extends Node {

        private TurnEvent event;    // reference of parent turnevent

        /**
         * Constructor
         *
         * @param p     the location of the turning node
         * @param event the parent turnevent
         */
        TurningNodes(Point2D p, TurnEvent event) {
            super(p, null, null);
            this.event = event;

            LogUtils.printGeneralMessage("New TurningNode object initiated: " + this + "! Hooked to TurnEvent " + event + ".");
        }

        /**
         * Method that returns the next node.
         *
         * @param reverse whether the arc is being reversed
         * @return node, next turning node
         */
        public Node getNextNode(boolean reverse) {
            return event.getNextNode(this);
        }

    }

    /**
     * An object that dictates all aspects of a turn.
     */
    public static class TurnEvent {

        private double radius;          // radius of turn

        private double target_heading;  // the target heading/angle
        private double current_heading; // the current heading/angle
        private double delta_theta;     // the change in theta per tick

        private boolean reverse_after_turning;  // whether travel in opp. dir. after turn
        private boolean reverse;                // whether travelling in opp. dir. right now

        private Paths origin;   // originating path of turn
        private Paths dest;     // destination path of turn

        private Intersection intersection;  // turning intersection
        private Arc2D arc;                  // arc of turning

        private PathIterator iterator;      // path iterator
        private ArrayList<TurningNodes> turning_nodes;  // turning node list

        /**
         * Constructor
         *
         * @param origin
         * @param dest
         * @param intersection
         * @param reverse
         * @param reverse_after_turning
         */
        public TurnEvent(Paths origin, Paths dest, Intersection intersection, boolean reverse, boolean reverse_after_turning) {

            LogUtils.printGeneralMessage("New TurnEvent object initiated: " + this + "!");

            /* instantiate instance variables */
            this.origin = origin;
            this.dest = dest;
            this.intersection = intersection;

            this.current_heading = origin.getHeading(reverse);
            this.target_heading = dest.getHeading(reverse_after_turning);

            this.turning_nodes = new ArrayList<>();

            this.reverse = reverse;
            this.reverse_after_turning = reverse_after_turning;

            // calculate theta and radius
            calcDeltaTheta();
            calcRadius();

            /* generate turning arc */
            this.arc = new Arc2D.Double(Arc2D.OPEN);
            this.arc.setArcByTangent(intersection.getJumpPoint(origin, !reverse),
                    intersection, intersection.getJumpPoint(dest, reverse_after_turning), radius);

            /* use a path iterator to find points on the path */
            this.iterator = new FlatteningPathIterator(arc.getPathIterator(null), 1f);
            iteratePath();

            // for DEBUG purposes only
            active_arc = this.arc;
            node_collection = this.turning_nodes;

        }

        /**
         * Method that finds some points on an arc path.
         */
        private void iteratePath() {

            LogUtils.printGeneralMessage("TurningNode " + this + " iterating arc points.");

            double[] coords = new double[6];

            /* iterate through segments and create new turning nodes */
            for (PathIterator i = iterator; !i.isDone(); i.next()) {

                i.currentSegment(coords);
                turning_nodes.add(new TurningNodes(new Point2D.Double(coords[0], coords[1]), this));

            }

            LogUtils.printGeneralMessage("TurningNode " + this + " iterated " + turning_nodes.size() + " arc points.");

        }

        /**
         * Wrapper method for calculating delta theta
         * (wraps getDifference()).
         */
        void calcDeltaTheta() {

            delta_theta = getDifference(target_heading, current_heading);

        }

        /**
         * Method that calculates the radius of the arc.
         */
        void calcRadius() {

            /* get origin and destination jump nodes */
            Node origin_node = intersection.getJumpPoint(origin, !reverse);
            Node dest_node = intersection.getJumpPoint(dest, reverse_after_turning);

            /* calculate partial theta values */
            double partial_theta = delta_theta / 2;
            double sin_partial_theta = Math.sin(partial_theta);
            radius = 1 / (sin_partial_theta / (origin_node.distance(dest_node) / 2));

            if (radius < 0) radius = -radius;

        }

        /**
         * Method that returns the next turning node in the list.
         *
         * @param node the current turning node
         * @return the next turning node
         */
        TurningNodes getNextNode(TurningNodes node) {

            try {
                return turning_nodes.get(
                        turning_nodes.indexOf(node) + 1
                );
            } catch (IndexOutOfBoundsException e) {

                LogUtils.printErrorMessage(e.getMessage());

                return null;
            }

        }

        /**
         * Method that returns the first node in the list.
         *
         * @return TurningNode, the first node
         */
        public TurningNodes getFirstNode() {
            return turning_nodes.get(0);
        }

        /**
         * Method that calculates and returns the length
         * of the turn.
         *
         * @return double, turning length
         */
        public double getTurnLength() {

            /* iterate through nodes array, add distances together */
            double total_distance = 0;
            for (int i = 0; i < turning_nodes.size() - 1; i++) {
                total_distance += turning_nodes.get(i).distance(turning_nodes.get(i + 1));
            }

            return total_distance;

        }
    }

    /**
     * Method that calculates and returns the angular difference
     * between two angles.
     *
     * @param a1    angle 1
     * @param a2    angle 2
     * @return  double, angular difference
     */
    private static double getDifference(double a1, double a2) {

        /* return minumum value, limit difference to [0, 360] */
        return Math.min((a1 - a2) < 0 ? a1 - a2 + 360 : a1 - a2, (a2 - a1) < 0 ? a2 - a1 + 360 : a2 - a1);
    }

}
