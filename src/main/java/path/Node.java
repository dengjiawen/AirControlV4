package main.java.path;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class Node extends Point2D implements Serializable {

    private double x;
    private double y;

    private Node next_node;
    private Node prev_node;

    protected NodeType type;

    protected Node(Point2D p, Node prev_node, Node next_node) {

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
