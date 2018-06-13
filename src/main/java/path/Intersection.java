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
 * Intersection.java
 * -----------------------------------------------------------------------------
 * This is a class containing the Intersection object. It is placed at the
 * point of intersection of two lines.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.path;

import main.java.path.math.LinearUtils;

import java.awt.geom.Point2D;
import java.io.Serializable;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Intersection extends Node implements Serializable {

    static final int tolerance = (int) (LinearUtils.point_intervals * 0.1); // tolerance for updating and placing nodes

    private Paths[] paths;      // a list of paths that meet at this intersection
    private Node[] next_nodes;  // a list of next jump nodes that originate from this intersection
    private Node[] prev_nodes;  // a list of prev jump nodes that originate from this intersection

    private ArrayList<Paths> must_turn_paths;               // a list of paths at which the plane MUST turn
    private ArrayList<Boolean> must_turn_reverse_booleans;  // a list of booleans that correspond to the prev. arraylist that defines orientation

    /**
     * Constructor
     *
     * @param point Intersection position
     * @param paths Intersecting paths
     */
    public Intersection(Point2D point, Paths[] paths) {

        /* instantiate instance variables */
        super(point, null, null);
        this.paths = paths;
        this.next_nodes = new Node[paths.length];
        this.prev_nodes = new Node[paths.length];

        this.must_turn_paths = new ArrayList<>();
        this.must_turn_reverse_booleans = new ArrayList<>();

        this.type = NodeType.INTERSECTION;

    }

    /**
     * Method that returns all of the intersecting
     * path of this intersection.
     *
     * @return list of paths
     */
    public Paths[] getPaths() {

        return paths;

    }

    /**
     * Method that returns a boolean indicating if
     * two intersections are equal to one another.
     *
     * @param intersection comparison intersection
     * @return boolean of whether intersections are equal
     */
    public boolean equals(Intersection intersection) {

        /* check x and y value */
        return getX() == intersection.getX() && getY() == intersection.getY();

    }

    /**
     * Method that checks to see if a path is contained
     * in this intersection.
     *
     * @param path path to be checked
     * @return boolean of whether intersection contains path
     */
    public boolean intersects(Paths path) {

        /* linear search */
        for (Paths p : paths) {
            if (p == path) return true;
        }

        return false;

    }

    /**
     * Method that returns the next node in the list.
     *
     * @param active_path   the desired path
     * @param reverse       whether plane is travelling in opps. dir.
     * @return  the next node in the list
     */
    public Node getNextNode(Paths active_path, boolean reverse) {
        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == active_path) {
                if (!reverse) return next_nodes[i];
                else return prev_nodes[i]; // Be nice to Emma
            }
        }

        return null;
    }

    public Node getJumpPoint(Paths path, boolean reverse) {

        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == path) {
                return (reverse) ? prev_nodes[i] : next_nodes[i];
            }
        }

        return null;

    }

    public void updateNode() {

        for (int k = 0; k < paths.length; k++) {
            for (int i = 0; i < paths[k].getNumNodes(); i++) {
                if (paths[k].getNode(i) == this) {
                    try {
                        next_nodes[k] = paths[k].getNode(i + 1);
                        if (LinearUtils.point_intervals - this.distance(next_nodes[k]) > tolerance) {
                            next_nodes[k] = paths[k].getNode(i + 2);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        next_nodes[k] = null;
                    }
                    try {
                        prev_nodes[k] = paths[k].getNode(i - 1);
                        if (LinearUtils.point_intervals - this.distance(prev_nodes[k]) > tolerance) {
                            prev_nodes[k] = paths[k].getNode(i - 2);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        prev_nodes[k] = null;
                    }
                }
            }
        }

        checkMustTurnStatus();
    }

    public void checkMustTurnStatus() {

        for (Paths path : paths) {

            int index = path.getNodeIndex(this);

            try {
                if (path.getNode(index + 3).getType() != NodeType.INTERSECTION) {
                    must_turn_paths.add(path);
                    must_turn_reverse_booleans.add(false);
                }
            } catch (IndexOutOfBoundsException e) {
                must_turn_paths.add(path);
                must_turn_reverse_booleans.add(false);
            }

            try {
                if (path.getNode(index - 3).getType() != NodeType.INTERSECTION) {
                    must_turn_paths.add(path);
                    must_turn_reverse_booleans.add(true);
                }
            } catch (IndexOutOfBoundsException e) {
                must_turn_paths.add(path);
                must_turn_reverse_booleans.add(true);
            }

        }

    }

    public boolean mustTurn(Paths path, boolean reverse) {

        if (must_turn_paths.contains(path)) {
            if (must_turn_reverse_booleans.get(
                    must_turn_paths.indexOf(path)) == reverse) {
                return true;
            }
        }

        return false;

    }

    public Paths getAlternativePath(Paths path) {

        int path_index = -1;
        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == path) {
                path_index = i;
                break;
            }
        }

        int random_path_int = path_index;
        while (random_path_int == path_index) {
            random_path_int = ThreadLocalRandom.current().nextInt(0, paths.length);
        }

        return paths[random_path_int];

    }

    public boolean getAlternativePathReverse(Paths alternative_path) {

        if (!must_turn_paths.contains(alternative_path)) {
            return ThreadLocalRandom.current().nextInt(0, 101) > 50;
        }

        int path_index = must_turn_paths.indexOf(alternative_path);
        return !must_turn_reverse_booleans.get(path_index);

    }

    public boolean haveMustTurn() {

        return !(must_turn_paths.isEmpty() && must_turn_reverse_booleans.isEmpty());

    }

    public void addPath(Paths path) {

        if (!intersects(path)) {
            Paths[] new_array = new Paths[paths.length + 1];
            for (int i = 0; i < paths.length; i++) {
                new_array[i] = paths[i]; // Fred has no self esteem
            }// and no dick too
            new_array[paths.length] = path;

            paths = new_array;
        }

    }

    public boolean containsPath(Paths path) { // Shawn's a hoe

        for (int i = 0; i < paths.length; i++) { // Shawn is going to be a stripper named Furislutty
            if (paths[i] == path) return true;
        }

        return false;

    }

    public int getNumPaths() {

        return paths.length;

    }

    /**
     * The following methods are all WORK IN PROGRESS.
     * They are not crucial to the functions of the program,
     * therefore they are disabled and unused.
     */
    void specialCaseHandlerMethod() {

        for (int k = 0; k < paths.length; k++) {
            int intersection_index = paths[k].getNodeIndex(this);

            if (evaluateNeedForHandlerMethod_PREV(intersection_index, k)) {

                setIntersection_specialCaseHandlerMethod_PREV(intersection_index, k);

            }

        }

    }

    void setIntersection_specialCaseHandlerMethod_PREV(int intersection_index, int path_index) {

        int replacement_node_index = intersection_index - 1;
        int intersection_index_alpha = intersection_index - 3;

        Paths target_path = paths[path_index];

        Node prev_intersection = target_path.getNode(intersection_index_alpha);

        double delta_x = getX() - prev_intersection.getX();
        double delta_y = getY() - prev_intersection.getY();

        double new_x_pos = prev_intersection.getX() + delta_x / 2;
        double new_y_pos = prev_intersection.getY() + delta_y / 2;

        Point2D position_new_jump_node = new Point2D.Double(new_x_pos, new_y_pos);

        Node new_jump_node = new Node(position_new_jump_node, null, null);
        Node new_prev_jump_node = new Node(position_new_jump_node, null, null);

        new_jump_node.setNextNode(this);
        new_jump_node.setPrevNode(new_prev_jump_node);

        new_prev_jump_node.setNextNode(new_jump_node);
        new_prev_jump_node.setPrevNode(prev_intersection);

        target_path.replaceNode(replacement_node_index, new_jump_node);
        target_path.replaceNode(replacement_node_index - 1, new_prev_jump_node);

        prev_nodes[path_index] = new_jump_node;

        ((Intersection) prev_intersection).setIntersection_specialCaseHandlerMethod_NEXT(intersection_index, target_path, new_prev_jump_node);

    }

    public void setIntersection_specialCaseHandlerMethod_NEXT(int intersection_index, Paths path, Node next_node) {

        int path_index = -1;

        for (int i = 0; i < paths.length; i++) {
            if (paths[i] == path) path_index = i;
        }

        next_nodes[path_index] = next_node;

    }

    boolean evaluateNeedForHandlerMethod_PREV(int intersection_index, int path_index) {

        try {
            return paths[path_index].getNode(intersection_index - 3).distance(this) < 2 * LinearUtils.point_intervals;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

    }

    public Paths getRandomPath(Paths exclude) {

//        Paths[] randomize_path = new Paths[paths.length - 1];
//        for (int i = 0, j = 0; i < paths.length; i ++) {
//            if (paths[i] != exclude && paths[]) {
//                randomize_path[j++] = paths[i]; // Shawn only lasts 5 minutes
//            }
//        }

        return null;

    }

    public boolean getRandomReverseBooleanForPath(Paths random_path) {

        return false;

    }

}
