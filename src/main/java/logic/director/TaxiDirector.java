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
 * TaxiDirector.java
 * -----------------------------------------------------------------------------
 * This is a director that controls the plane when the plane is travelling
 * normally in a straight line.
 * <p>
 * This class is a part of CoreLogic and inherits the Director ADT.
 * -----------------------------------------------------------------------------
 */

package main.java.logic.director;

import main.java.common.LogUtils;
import main.java.common.ThreadUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.logic.Airplane;
import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;
import main.java.ui.RenderUtils;

import javax.swing.*;

import java.awt.geom.Point2D;

public class TaxiDirector extends Director {

    /**
     * Static block for initializing TaxiDirector constants
     */
    static {

        LogUtils.printGeneralMessage("Initializing TaxiDirector...");
        tolerance = Constants.getInt("tolerance", Definitions.ZERO_DAY_PATCH);

    }

    private static int tolerance;   // determines the tolerance before snapping occurs

    private boolean recalculate_telemetry;      // whether plane telemetry needs recalculation
    private boolean reverse;                    // whether plane is travelling in oppos. dir. to runway
    private boolean reverse_after_intersection; // whether plane should travel in oppos. dir. after turning

    private Paths current_path;     // reference of current path
    private Paths target_path;      // reference of target path (for turning)

    private Node current_node;      // reference of currently associated node
    private Node target_node;       // reference of target node

    private double tick_x;          // delta x per tick
    private double tick_y;          // delta y per tick

    /**
     * Constructor, takes the index of starting node as a parameter.
     *
     * @param plane
     * @param current_path
     * @param reverse
     * @param starting_node_index
     */
    public TaxiDirector(Airplane plane, Paths current_path, boolean reverse, int starting_node_index) {

        /* call on overloaded constructor with proper node reference */
        this(plane, current_path, reverse, current_path.getNode(starting_node_index));

    }

    /**
     * Constructor, takes a reference of the starting node as a parameter.
     *
     * @param plane
     * @param current_path
     * @param reverse
     * @param start_node
     */
    TaxiDirector(Airplane plane, Paths current_path, boolean reverse, Node start_node) {

        super(plane);

        LogUtils.printGeneralMessage("New TaxiDirector initiated: " + this + "! Hooked onto plane " + plane + ".");

        /* initialize instance variables */
        this.current_path = current_path;
        this.target_path = null;

        this.current_node = start_node;
        this.target_node = start_node;

        this.reverse = reverse;

        this.recalculate_telemetry = true;

        /* initialize plane position */
        plane.setPosition(current_node);
        plane.setHeading(current_path.getHeading(reverse));

        /* initialize tick update timer */
        tick_update = new Timer(1000 / tick_length, e ->
                /* throw work to position worker thread */
                ThreadUtils.position_worker.submit(() -> {
                    tickUpdate();
                }));

    }

    /**
     * Overriden tickUpdate method.
     */
    @Override
    void tickUpdate() {

        /* if telemetry needs recalculation, recalculate telemetry */
        if (recalculate_telemetry) recalcTelemetry();

        /* update plane position */
        plane.setPosition(
                new Point2D.Double(plane.getPosition().getX() + tick_x, plane.getPosition().getY() + tick_y));

        /* if plane distance to target node is smaller than tolerance,
           move plane to target node and recalculate telemetry. */
        if (plane.getPosition().distance(target_node) <= tolerance) {
            plane.setPosition(target_node);
            current_node = target_node;
            recalculate_telemetry = true;
        }

        /* invoke canvas repaint */
        RenderUtils.invokeRepaint();

    }

    /**
     * Method that recalculates the telemetry (tick x and tick y).
     */
    private void recalcTelemetry() {

        LogUtils.printGeneralMessage("Director " + this + " is requesting a telemetry recalculation.");

        /* get the next node */
        /* intersection nodes require special handling */
        if (current_node.getType() == Node.NodeType.INTERSECTION) {
            target_node = ((Intersection) current_node).getNextNode(current_path, reverse);
        } else {
            target_node = current_node.getNextNode(reverse);
        }

        LogUtils.printGeneralMessage("Director " + this + " is recalculating telemetry from " + current_node + " to " + target_node + ".");

        /* if there are no more nodes, stop director */
        if (target_node == null) {

            LogUtils.printErrorMessage("Director " + this + " had stopped because the target node do not exist.");

            stopDirector();
            return;
        }

        /* the amount of time it will take to complete the travel between nodes */
        double target_time_in_seconds = current_node.distance(target_node) / (plane.getSpeed());
        /* if this time is zero, assume plane is at target node */
        if (target_time_in_seconds == 0) {

            LogUtils.printGeneralMessage("Director " + this + ": nodes are the same? Moving on to the next node.");

            current_node = target_node;
            recalcTelemetry();
            return;
        }

        /* get delta x and delta y, and divide by target time */
        tick_x = (target_node.getX() - current_node.getX()) / (target_time_in_seconds * 1000 / tick_length);
        tick_y = (target_node.getY() - current_node.getY()) / (target_time_in_seconds * 1000 / tick_length);

        LogUtils.printGeneralMessage("Director " + this + " had finished calculations: result is (" + tick_x + ", " + tick_y + ").");

        recalculate_telemetry = false;

        /* look ahead for possible turns */
        lookAhead();

    }

    /**
     * Method that looks ahead for possible turns.
     */
    private void lookAhead() {

        LogUtils.printGeneralMessage("Director " + this + " is requesting to look ahead for possible turns.");

        /* if the director is set to turn, and the plane had arrived at the intersection, switch director */
        if (current_node.getNextNode(reverse).getType() == Node.NodeType.INTERSECTION && target_path != null) {

            Intersection target_intersection = (Intersection) current_node.getNextNode(reverse);
            if (target_intersection.intersects(target_path)) {

                LogUtils.printGeneralMessage("Director " + this + " is requesting to turn onto path " + target_path + " via " + target_intersection + ".");

                /* set plane's director to a turn director for turning */
                plane.setActiveDirector(
                        new TurnDirector(plane, target_intersection, current_path, target_path, reverse, reverse_after_intersection));

                return;
            }
        }

        /* check if the plane must turn (if there are no more nodes on the path */
        if (current_node.getNextNode(reverse).getType() == Node.NodeType.INTERSECTION && target_path == null) {
            Intersection target_intersection = (Intersection) current_node.getNextNode(reverse);

            if (target_intersection.mustTurn(current_path, reverse)) {

                target_path = target_intersection.getAlternativePath(current_path);
                reverse_after_intersection = target_intersection.getAlternativePathReverse(target_path);

                LogUtils.printGeneralMessage("Director " + this + " is preparing to turn onto path " + target_path + " via " + target_intersection + ".");

                lookAhead();
            }
        }

    }

    /**
     * Overriden startDirector method.
     */
    @Override
    public void startDirector() {

        LogUtils.printGeneralMessage("Director " + this + " had been started!");
        tick_update.start();

    }

    /**
     * Overriden stopDirector method.
     */
    @Override
    public void stopDirector() {

        LogUtils.printGeneralMessage("Director " + this + " had been stopped!");
        tick_update.stop();

    }

    /**
     * Overriden handOff method.
     * @param new_director the new director
     */
    @Override
    public void handOff(Director new_director) {}

    /**
     * Method that tells the director to turn at a certain path.
     * @param path  the path to turn
     * @param reverse_after_intersection    whether to reverse after turning
     */
    public void setTargetPath(Paths path, boolean reverse_after_intersection) {

        LogUtils.printGeneralMessage("A new target path " + path + "  had been set for director " + this + "!");

        this.target_path = path;
        this.reverse_after_intersection = reverse_after_intersection;

    }

}
