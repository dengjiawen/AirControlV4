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
 * TurnDirector.java
 * -----------------------------------------------------------------------------
 * This is a director that controls the plane when it is turning from one
 * path to another at an intersection.
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
import main.java.path.math.CircleUtils;
import main.java.ui.RenderUtils;

import javax.swing.*;
import java.awt.geom.Point2D;

public class TurnDirector extends Director {

    /**
     * Static block for initializing TurnDirector constants
     */
    static {

        LogUtils.printGeneralMessage("Initializing TurnDirector...");
        tolerance = Constants.getInt("tolerance", Definitions.ZERO_DAY_PATCH_SUPPLEMENTARY);

    }


    private static int tolerance;   // determines the tolerance before snapping occurs

    private CircleUtils.TurnEvent active_event;     // turn event object

    private boolean recalculate_telemetry;          // whether plane telemetry needs recalculation
    private boolean reverse_after_intersection;     // whether plane should travel in oppos. dir. after turning

    private Node target_node;   // reference of target node
    private Node current_node;  // reference of currently associated node

    private Paths origin;       // originating path
    private Paths destination;  // destination path

    private Intersection intersection;  // turning intersection

    private double target_heading;      // the target heading/angle
    private double current_heading;     // the current heading/angle

    private double tick_theta;  // delta angle per tick

    private double tick_x;      // delta x per tick
    private double tick_y;      // delta y per tick

    /**
     * Constructor
     *
     * @param plane
     * @param intersection
     * @param origin
     * @param dest
     * @param reverse
     * @param reverse_after_intersection
     */
    TurnDirector(Airplane plane, Intersection intersection, Paths origin, Paths dest, boolean reverse, boolean reverse_after_intersection) {

        super(plane);

        LogUtils.printGeneralMessage("New TurnDirector initiated: " + this + "! Hooked onto plane " + plane + ".");

        /* initialize instance variables */
        this.active_event = new CircleUtils.TurnEvent(origin, dest, intersection, reverse, reverse_after_intersection);

        this.current_heading = origin.getHeading(reverse);
        this.target_heading = dest.getHeading(reverse_after_intersection);

        this.origin = origin;
        this.destination = dest;
        this.intersection = intersection;

        this.reverse_after_intersection = reverse_after_intersection;

        this.current_node = active_event.getFirstNode();
        this.target_node = current_node.getNextNode(false);

        recalcTelemetry(false);
        calcTickTheta();

        this.recalculate_telemetry = false;

        /* initialize tick update timer */
        this.tick_update = new Timer(1000 / tick_length, e ->
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
        if (recalculate_telemetry) {
            recalcTelemetry(true);
        }

        /* update plane position */
        plane.setPosition(
                new Point2D.Double(plane.getPosition().getX() + tick_x, plane.getPosition().getY() + tick_y));
        /* update plane heading/angle */
        plane.setHeading(plane.getHeading() + tick_theta);

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
    private void recalcTelemetry(boolean advance_node) {

        LogUtils.printGeneralMessage("Director " + this + " is requesting a telemetry recalculation.");

        /* if node must be advanced, move on to the next node */
        if (advance_node) target_node = current_node.getNextNode(false);

        /* if the end of the turn had been reached, switch back to TaxiDirector */
        if (target_node == null) {

            target_node = intersection.getNextNode(destination, reverse_after_intersection);

            plane.setPosition(target_node);
            plane.setHeading(target_heading);

            LogUtils.printGeneralMessage("Director " + this + " is reporting that the turn had been completed.");

            plane.setActiveDirector(new TaxiDirector(plane, destination, reverse_after_intersection, target_node));

        }

        /* the amount of time it will take to complete the travel between nodes */
        double target_time_in_seconds = current_node.distance(target_node) / (plane.getSpeed());

        /* if this time is zero, assume plane is at target node */
        if (target_time_in_seconds == 0) {

            LogUtils.printGeneralMessage("Director " + this + ": nodes are the same? Moving on to the next node.");
            current_node = target_node;
            recalcTelemetry(true);
            return;
        }

        /* get delta x and delta y, and divide by target time */
        tick_x = (target_node.getX() - current_node.getX()) / (target_time_in_seconds * 1000 / tick_length);
        tick_y = (target_node.getY() - current_node.getY()) / (target_time_in_seconds * 1000 / tick_length);

        LogUtils.printGeneralMessage("Director " + this + " had finished calculations: result is (" + tick_x + ", " + tick_y + ").");

        recalculate_telemetry = false;

    }

    /**
     * Method that calculates the tick theta (change in angle per tick)
     */
    private void calcTickTheta() {

        LogUtils.printGeneralMessage("Director " + this + " is starting a theta recalculation.");

        /* get the total turn distance and the change in heading */
        double total_distance = active_event.getTurnLength();
        double delta_heading = target_heading - current_heading;

        /* correct angle to keep it at [-360, 360] */
        if (delta_heading < -2 * Math.PI) {
            delta_heading += 2 * Math.PI;
        } else if (delta_heading > 2 * Math.PI) {
            delta_heading -= 2 * Math.PI;
        }

        /* do a bunch of fancy trig here to calculate the change in angle per tick */
        if (delta_heading > Math.PI) {
            tick_theta = -(Math.PI * 2 - delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        } else if (delta_heading < -Math.PI) {
            tick_theta = (Math.PI * 2 + delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        } else {
            tick_theta = (delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        }

        LogUtils.printDebugMessage("Director " + this + " is reporting a tick theta of " + tick_theta + ".");

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


}
