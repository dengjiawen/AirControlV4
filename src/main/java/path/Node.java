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
 * Node.java
 * -----------------------------------------------------------------------------
 * This is a position object, used for guiding the plane.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.path;

import main.java.common.LogUtils;

import java.awt.geom.Point2D;

import java.io.Serializable;

public class Node extends Point2D implements Serializable {

    private double x;   // x pos of the node
    private double y;   // y pos of the node

    private Node next_node; // the previous node
    private Node prev_node; // the next node

    NodeType type;  // the type of node

    /**
     * Default constructor
     */
    private Node() {}

    /**
     * Constructor
     */
    protected Node(Point2D p, Node prev_node, Node next_node) {

        LogUtils.printGeneralMessage("New node " + this + " created at " + p + "!");

        /* instantiate instance variables */
        setLocation(p);
        this.next_node = next_node;
        this.prev_node = prev_node;

        this.type = NodeType.REGULAR_NODE;

    }

    /**
     * Method that returns the next node.
     * @param reverse   whether plane is travelling in opps. dir.
     * @return
     */
    public Node getNextNode(boolean reverse) {

        if (!reverse) {
            return next_node;
        } else {
            return prev_node;
        }

    }

    /**
     * Method that sets the next node.
     * @param next_node
     */
    void setNextNode(Node next_node) {
        this.next_node = next_node;
    }

    /**
     * Method that sets the previous node.
     * @param prev_node
     */
    void setPrevNode(Node prev_node) {
        this.prev_node = prev_node;
    }

    /**
     * Method that returns the node type.
     * @return  NodeType enum
     */
    public NodeType getType() {
        return type;
    }

    /**
     * Method that returns the x pos of the node.
     * @return double, x pos
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Method that returns the y pos of the node.
     * @return double, y pos
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Method that sets the location of the node.
     * @param p the point, new pos
     */
    @Override
    public void setLocation(Point2D p) {
        this.x = (float) p.getX();
        this.y = (float) p.getY();
    }

    /**
     * Method that sets the location of the node.
     * @param x x pos, new pos
     * @param y y pos, new pos
     */
    @Override
    public void setLocation(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    /**
     * Overriden toString method.
     * @return  String representation of object
     */
    public String toString() {
        return super.toString() + " " + getX() + ", " + getY();
    }

    /**
     * Enumerated type defining the type of node.
     */
    public enum NodeType implements Serializable {

        REGULAR_NODE, INTERSECTION

    }

}
