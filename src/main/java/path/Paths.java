package main.java.path;

import main.java.path.math.AngleUtils;
import main.java.path.math.LinearUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public enum Paths implements Serializable {

    rwy26(MapUtils.rwy26, "RWY 26"), rwy35(MapUtils.rwy35, "RWY 35"), rwy3L(MapUtils.rwy3L, "RWY 3L"), rwy3R(MapUtils.rwy3R, "RWY 3R"),

    taxiE(MapUtils.taxiE, "TAXIWAY E"), taxiD(MapUtils.taxiD, "TAXIWAY D"),
    taxiF(MapUtils.taxiF, "TAXIWAY F"), taxiF1(MapUtils.taxiF1, "TAXIWAY F1"),
    taxiQ(MapUtils.taxiQ, "TAXIWAY Q"), taxiH(MapUtils.taxiH, "TAXIWAY H"),

    taxiC1(MapUtils.taxiC1, "TAXIWAY C1"), taxiC2(MapUtils.taxiC2, "TAXIWAY C2"),
    taxiC3(MapUtils.taxiC3, "TAXIWAY C3"),

    taxiA(MapUtils.taxiA, "TAXIWAY A"),

    taxiP(MapUtils.taxiP, "TAXIWAY P"), taxiB(MapUtils.taxiB, "TAXIWAY B");

    private static final String MAP_IDENTIFIER = "XfSlMhgAsUAw1aj1G3sD5";
    private static boolean MAP_UP_TO_DATE;

    public transient Color debug_color;

    private Line2D path;
    private Node[] nodes;
    private String name; //(.)(.)

    private double heading;

    Paths(Line2D path, String name) {

        this.path = path;
        this.name = name;

        this.heading = AngleUtils.getAngle(path);
        this.debug_color = new Color((int) (Math.random() * 0x1000000));

    }

    public Line2D getPath() {
        return new Line2D.Double(path.getX1(), path.getY1(), path.getX2(), path.getY2()); // What is the meaning of life?
    }

    void setIntersections(Intersection[] intersections) {

        this.nodes = new Node[(intersections.length) * 3 + 2];
        for (int i = 0, j = 1; i < intersections.length; i++, j += 3) {

            nodes[j] = new Node(
                    LinearUtils.getJumpPoint(intersections[i], this, LinearUtils.Orientation.PREVIOUS),
                    nodes[j - 1], intersections[i]);

            try {
                nodes[j - 1].setNextNode(nodes[j]);
            } catch (NullPointerException e) {
            }

            nodes[j + 1] = intersections[i];
            nodes[j + 2] = new Node(
                    LinearUtils.getJumpPoint(intersections[i], this, LinearUtils.Orientation.NEXT),
                    nodes[j + 1], null);
        }

        this.nodes[0] = new Node(path.getP1(), null, this.nodes[1]);
        this.nodes[this.nodes.length - 1] = new Node(path.getP2(),
                this.nodes[this.nodes.length - 2], null);

        this.nodes[this.nodes.length - 2].setNextNode(this.nodes[this.nodes.length - 1]);

        sortNodeArray(nodes);
        relinkNodes(nodes);

        for (Node node : nodes) {
            System.out.println("This node: " + node + " | Next node: " + node.getNextNode(false));
        }

    }

    void sortNodeArray(Node[] nodes) {

        for (int i = 1; i < nodes.length - 1; i++) {

            int index = i;

            for (int j = i + 1; j < nodes.length; j++) {
                if (nodes[j].distance(nodes[0]) < nodes[index].distance(nodes[0])) {
                    index = j;
                }
            }

            if (index != i) {
                Node temporary_node = nodes[i];
                nodes[i] = nodes[index];
                nodes[index] = temporary_node;
            }

        }

    }

    void relinkNodes(Node[] nodes) {

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getType() != Node.NodeType.INTERSECTION) {

                try {
                    if (nodes[i].getNextNode(false) != nodes[i + 1]) {
                        nodes[i].setNextNode(nodes[i + 1]);
                    }
                } catch (IndexOutOfBoundsException e) {
                }

                try {
                    if (nodes[i].getNextNode(true) != nodes[i - 1]) {
                        nodes[i].setPrevNode(nodes[i - 1]);
                    }
                } catch (IndexOutOfBoundsException e) {
                }
            }

        }

    }

    public Node getNode(int index) {
        return nodes[index];
    }

    public void replaceNode(int index, Node node) {
        nodes[index] = node;
    }

    public int getNumNodes() { // Boop.
        return nodes.length;
    }

    public int getNodeIndex(Node target) {

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == target) return i;
        }

        return -1;

    }

    public double getHeading(boolean reverse) {
        if (reverse)
            return ((heading + Math.PI > Math.PI) ? heading - Math.PI : heading + Math.PI);
        else
            return heading;
    }

    public String getName() {
        return name;
    }

    public static void saveTelemetryData() {

        String directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        directory += "/AirControlV3/telemetry/";

        Path dir_path = java.nio.file.Paths.get(directory);
        try {
            System.out.println(Files.createDirectories(dir_path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Paths path : values()) {

            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(new FileOutputStream(directory + "telemetry_" + path.toString() + ".path"));
                out.writeObject(MAP_IDENTIFIER);
                out.writeObject(path.nodes);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public static boolean readTelemetryData() {

        String directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        directory += "/AirControlV3/telemetry/";

        for (Paths path : values()) {

            ObjectInputStream in = null;
            try {
                in = new ObjectInputStream(new FileInputStream(directory + "telemetry_" + path.toString() + ".path"));

                String map_identifier = (String) in.readObject();
                MAP_UP_TO_DATE = map_identifier.equals(MAP_IDENTIFIER);
                if (!MAP_UP_TO_DATE) return false;

                Node[] nodes = (Node[]) in.readObject();
                path.nodes = nodes;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        return MAP_UP_TO_DATE;

    }

}
