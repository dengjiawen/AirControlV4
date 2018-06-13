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

        /* instantiate instance variables */
        setLocation(p);
        this.next_node = next_node;
        this.prev_node = prev_node;

        this.type = NodeType.REGULAR_NODE;

    }

    public Node getNextNode(boolean reverse) {

        if (!reverse) {
            return next_node;
        } else {
            return prev_node;
        }

    }

    public void setNextNode(Node next_node) {
        this.next_node = next_node;
    }

    public void setPrevNode(Node prev_node) {
        this.prev_node = prev_node;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setLocation(Point2D p) {
        this.x = (float) p.getX();
        this.y = (float) p.getY();
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public String toString() {
        return super.toString() + " " + getX() + ", " + getY();
    }

    public enum NodeType implements Serializable {

        REGULAR_NODE, INTERSECTION

    }

}
