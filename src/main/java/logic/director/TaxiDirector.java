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
 * Director.java
 * -----------------------------------------------------------------------------
 * This is an abstract data type (ADT) that defines how a director should
 * behave. A director directs the position of an Airplane object, providing
 * the Canvas object with up to date position and angle information.
 * <p>
 * This class is a part of CoreLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic.director;

import main.java.common.ThreadUtils;
import main.java.logic.Airplane;
import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;
import main.java.ui.RenderUtils;

import javax.swing.*;
import java.awt.geom.Point2D;

public class TaxiDirector extends Director {

    protected static final int tolerance = 2;

    private boolean recalculate_telemetry;

    private boolean reverse;
    private boolean reverse_after_intersection;

    private Paths current_path;
    private Paths target_path;

    private Node current_node;
    private Node target_node;

    private double tick_x;
    private double tick_y;

    public TaxiDirector(Airplane plane, Paths current_path, boolean reverse, int starting_node_index) {

        this(plane, current_path, reverse, current_path.getNode(starting_node_index));

    }

    public TaxiDirector(Airplane plane, Paths current_path, boolean reverse, Node start_node) {

        super(plane);

        this.current_path = current_path;
        this.target_path = null;

        this.current_node = start_node;
        this.target_node = start_node;

        this.reverse = reverse;

        this.recalculate_telemetry = true;

        plane.setPosition(current_node);
        plane.setHeading(current_path.getHeading(reverse));

        tick_update = new Timer(1000 / tick_length, e -> ThreadUtils.position_worker.submit(() -> {
            tickUpdate();
        }));

    }

    @Override
    public void tickUpdate() {

        if (recalculate_telemetry) recalcTelemetry();

        plane.setPosition(
                new Point2D.Double(plane.getPosition().getX() + tick_x, plane.getPosition().getY() + tick_y));

        if (plane.getPosition().distance(target_node) <= tolerance) {
            plane.setPosition(target_node);
            current_node = target_node;
            recalculate_telemetry = true;
        }

        RenderUtils.invokeRepaint();

    }

    private void recalcTelemetry() {

        if (current_node.getType() == Node.NodeType.INTERSECTION) {
            target_node = ((Intersection) current_node).getNextNode(current_path, reverse);
        } else {
            target_node = current_node.getNextNode(reverse);
        }

        if (target_node == null) {
            stopDirector();
            return;
        }

        double target_time_in_seconds = current_node.distance(target_node) / (plane.getSpeed());

        if (target_time_in_seconds == 0) {
            current_node = target_node;
            recalcTelemetry();
            return;
        }

        tick_x = (target_node.getX() - current_node.getX()) / (target_time_in_seconds * 1000 / tick_length);
        tick_y = (target_node.getY() - current_node.getY()) / (target_time_in_seconds * 1000 / tick_length);

        recalculate_telemetry = false;

        lookAhead();

    }

    private void lookAhead() {

        if (current_node.getNextNode(reverse).getType() == Node.NodeType.INTERSECTION && target_path != null) {

            Intersection target_intersection = (Intersection) current_node.getNextNode(reverse);
            if (target_intersection.intersects(target_path)) {
                System.out.println("Switching director");
                plane.setActiveDirector(
                        new TurnDirector(plane, target_intersection, current_path, target_path, reverse, reverse_after_intersection));

                return;
            }
        }

        if (current_node.getNextNode(reverse).getType() == Node.NodeType.INTERSECTION && target_path == null) {
            Intersection target_intersection = (Intersection) current_node.getNextNode(reverse);

            if (target_intersection.mustTurn(current_path, reverse)) {
                System.out.println("FINDING ALTERNATIVES");
                target_path = target_intersection.getAlternativePath(current_path);
                reverse_after_intersection = target_intersection.getAlternativePathReverse(target_path);
                System.out.println("FOUND! " + target_path + ", " + reverse_after_intersection);

                lookAhead();
            }
        }

    }

    @Override
    public void startDirector() {

        tick_update.start();

    }

    @Override
    public void stopDirector() {

        tick_update.stop();

    }

    @Override
    public void handOff(Director new_director) {


    }

    public void setTargetPath(Paths path, boolean reverse_after_intersection) {
        this.target_path = path;
        this.reverse_after_intersection = reverse_after_intersection;
    }

}
