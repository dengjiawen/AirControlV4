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
 * Paths.java
 * -----------------------------------------------------------------------------
 * This enumerated type manages all of the possible paths that the plane can
 * travel on.
 * <p>
 * This class is a part of AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.path;

import main.java.common.LogUtils;
import main.java.constants.Definitions;
import main.java.constants.ParseUtils;
import main.java.path.math.AngleUtils;
import main.java.path.math.LinearUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public enum Paths implements Serializable {

    /**
     * All of the runways and taxiways
     */
    rwy26(MapUtils.rwy26, "RWY 26"), rwy35(MapUtils.rwy35, "RWY 35"), rwy3L(MapUtils.rwy3L, "RWY 3L"), rwy3R(MapUtils.rwy3R, "RWY 3R"),

    taxiE(MapUtils.taxiE, "TAXIWAY E"), taxiD(MapUtils.taxiD, "TAXIWAY D"),
    taxiF(MapUtils.taxiF, "TAXIWAY F"), taxiF1(MapUtils.taxiF1, "TAXIWAY F1"),
    taxiQ(MapUtils.taxiQ, "TAXIWAY Q"), taxiH(MapUtils.taxiH, "TAXIWAY H"),

    taxiC1(MapUtils.taxiC1, "TAXIWAY C1"), taxiC2(MapUtils.taxiC2, "TAXIWAY C2"),
    taxiC3(MapUtils.taxiC3, "TAXIWAY C3"),

    taxiA(MapUtils.taxiA, "TAXIWAY A"),

    taxiP(MapUtils.taxiP, "TAXIWAY P"), taxiB(MapUtils.taxiB, "TAXIWAY B");


    /* a unique identifier that informs the program whether the config files are up to date */
    private static final String MAP_IDENTIFIER = ParseUtils.parseString(Definitions.CORE_CONSTANTS.getPath(), "ConfigFileIdentifier");
    private static boolean MAP_UP_TO_DATE;

    /* color for the Paths, debugging purposes only */
    public transient Color debug_color;

    private Line2D path;    // Line2D representation of the Path object
    private Node[] nodes;   // An array of linked nodes
    private String name;    // String of the name of the Path

    private double heading; // the heading of the Path

    /**
     * Constructor
     *
     * @param path Line2D representation of the Path in MapUtils
     * @param name Name of the Path
     */
    Paths(Line2D path, String name) {

        LogUtils.printGeneralMessage("Paths enum " + this + " is being initiated!");

        /* initialize instance variables */
        this.path = path;
        this.name = name;

        this.heading = AngleUtils.getAngle(path);
        this.debug_color = new Color((int) (Math.random() * 0x1000000));

    }

    /**
     * Method that returns the Line2D representation of the Path enum
     *
     * @return Line2D
     */
    public Line2D getPath() {
        return new Line2D.Double(path.getX1(), path.getY1(), path.getX2(), path.getY2());
    }

    /**
     * Method that updates the Node array with newly calculated
     * intersections from MapUtils.
     *
     * @param intersections
     */
    void setIntersections(Intersection[] intersections) {

        LogUtils.printErrorMessage("Setting intersection array " +this + " for path " + this + ".");

        this.nodes = new Node[(intersections.length) * 3 + 2];

        /* calculate jump nodes for every intersection */
        for (int i = 0, j = 1; i < intersections.length; i++, j += 3) {

            nodes[j] = new Node(
                    LinearUtils.getJumpPoint(intersections[i], this, LinearUtils.Orientation.PREVIOUS),
                    nodes[j - 1], intersections[i]);
            LogUtils.printErrorMessage("Found PREVIOUS jump node " + nodes[j] + " for intersection " + intersections + ".");

            /* link jump nodes to intersection */
            try {
                nodes[j - 1].setNextNode(nodes[j]);
            } catch (NullPointerException e) {
                LogUtils.printErrorMessage("Paths, " + e.getMessage() + ", error is ignored because it is irrelevant.");
            }

            nodes[j + 1] = intersections[i];
            nodes[j + 2] = new Node(
                    LinearUtils.getJumpPoint(intersections[i], this, LinearUtils.Orientation.NEXT),
                    nodes[j + 1], null);
            LogUtils.printErrorMessage("Found NEXT jump node " + nodes[j+2] + " for intersection " + intersections + ".");
        }

        /* set the first node to the starting node of the Path */
        this.nodes[0] = new Node(path.getP1(), null, this.nodes[1]);

        /* set the last jump node to the last node of the Path */
        this.nodes[this.nodes.length - 1] = new Node(path.getP2(),
                this.nodes[this.nodes.length - 2], null);

        /* link first and last nodes */
        this.nodes[this.nodes.length - 2].setNextNode(this.nodes[this.nodes.length - 1]);

        /* sort Node array */
        sortNodeArray(nodes);
        /* relink all Nodes */
        relinkNodes(nodes);

        /* print out each node for debug purposes */
        for (Node node : nodes) {
            LogUtils.printDebugMessage("This node: " + node + " | Next node: " + node.getNextNode(false));
        }

    }

    /**
     * Method that iterates through a node array and sort the
     * nodes based on their distance from the starting node.
     * @param nodes
     */
    void sortNodeArray(Node[] nodes) {

        LogUtils.printGeneralMessage("Now selection sorting node array " + nodes + ".");

        /* selection sort through the nodes */
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

        LogUtils.printGeneralMessage("Selection sort for node array " + nodes + " complete.");

    }

    /**
     * Method that relinks all of the nodes after sorting.
     * @param nodes
     */
    void relinkNodes(Node[] nodes) {

        LogUtils.printGeneralMessage("Relinking nodes in node array " + nodes + ".");

        for (int i = 0; i < nodes.length; i++) {

            if (nodes[i].getType() != Node.NodeType.INTERSECTION) {

                try {
                    if (nodes[i].getNextNode(false) != nodes[i + 1]) {

                        /* link node at index i to index i + 1 for next */
                        nodes[i].setNextNode(nodes[i + 1]);

                        LogUtils.printDebugMessage("Linking node " + nodes[i] + " to node " + nodes[i + 1] + ", NEXT.");
                    }
                } catch (IndexOutOfBoundsException e) {
                    LogUtils.printErrorMessage(e.getMessage());
                }

                try {
                    if (nodes[i].getNextNode(true) != nodes[i - 1]) {

                        /* link node at index i to index i - 1 for prev */
                        nodes[i].setPrevNode(nodes[i - 1]);

                        LogUtils.printDebugMessage("Linking node " + nodes[i] + " to node " + nodes[i - 1] + ", PREV.");
                    }
                } catch (IndexOutOfBoundsException e) {
                    LogUtils.printErrorMessage(e.getMessage());
                }
            }

        }

    }

    /**
     * Method that returns the node in the Paths at a certain
     * index.
     * @param index
     * @return
     */
    public Node getNode(int index) {
        return nodes[index];
    }

    /**
     * Method that replaces the node at a given index
     * with a given Node object.
     * @param index
     * @param node
     */
    public void replaceNode(int index, Node node) {

        LogUtils.printDebugMessage("Node " + nodes[index] + " is being replaced with Node " + node + " in Path " + this + ".");

        nodes[index] = node;
    }

    /**
     * Method that gets the number of nodes in the
     * Path.
     * @return
     */
    public int getNumNodes() {
        return nodes.length;
    }

    /**
     * Method that gets the index of a target node
     * in the Path.
     * @param target
     * @return
     */
    public int getNodeIndex(Node target) {

        /* iterate through array until the target node
         * is found. */
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == target) return i;
        }

        /* return -1 if node is not found */
        return -1;

    }

    /**
     * Method that returns the heading of the Path.
     * @param reverse   whether the plane is travelling in oppos. dir.
     * @return
     */
    public double getHeading(boolean reverse) {

        /* if heading is reversed, subtract or add by 180 DEG depending on
         * whether the angle + 180 > 180 (keeping angle between -180 and 180) */
        if (reverse)
            return ((heading + Math.PI > Math.PI) ? heading - Math.PI : heading + Math.PI);
        else
            return heading;
    }

    /**
     * Method that returns the name of the Path.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Method that saves calculated telementry data to file.
     */
    public static void saveTelemetryData() {

        /* get save directory by using the default path from JFileChooser */
        String directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        directory += "/AirControlV3/telemetry/";

        LogUtils.printGeneralMessage("Attempting to write calculated telemetry files to " + directory + ".");

        Path dir_path = java.nio.file.Paths.get(directory);
        try {
            /* try to write to directory to see if directory is writable */
            Files.createDirectories(dir_path);
        } catch (IOException e) {
            LogUtils.printErrorMessage(e.getMessage());
            LogUtils.printErrorMessage("Error writing to " + directory + ". Writing operation might fail.");
            return;
        }

        /* begin writing Paths to file */
        for (Paths path : values()) {

            ObjectOutputStream out = null;
            try {
                /* write MAP_IDENTIFIER and node arrays to file */
                LogUtils.printGeneralMessage("Starting to write Path " + path + " to file with identifier " + MAP_IDENTIFIER + ".");
                out = new ObjectOutputStream(new FileOutputStream(directory + "telemetry_" + path.toString() + ".path"));
                out.writeObject(MAP_IDENTIFIER);
                out.writeObject(path.nodes);
            } catch (FileNotFoundException e) {
                LogUtils.printErrorMessage(e.getMessage());
                LogUtils.printErrorMessage("Error writing Path to " + directory + ". Will give up.");
                return;
            } catch (IOException e) {
                LogUtils.printErrorMessage(e.getMessage());
                LogUtils.printErrorMessage("Error writing Path to " + directory + ". Will give up.");
                return;
            }

        }

    }

    /**
     * Method that read the telementry data to file.
     * @return  a boolean indicating whether the file was successfully read
     */
    public static boolean readTelemetryData() {

        /* get save directory by using the default path from JFileChooser */
        String directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        directory += "/AirControlV3/telemetry/";

        LogUtils.printGeneralMessage("Attempting to read calculated telemetry files from " + directory + ".");

        /* begin reading Paths */
        for (Paths path : values()) {

            ObjectInputStream in = null;
            try {

                /* read MAP_IDENTIFIER and node arrays */
                LogUtils.printGeneralMessage("Starting to read Path " + path + ".");

                in = new ObjectInputStream(new FileInputStream(directory + "telemetry_" + path.toString() + ".path"));

                String map_identifier = (String) in.readObject();
                LogUtils.printGeneralMessage("Checking saved identifier " + map_identifier + " with identifier " + MAP_IDENTIFIER + ".");
                MAP_UP_TO_DATE = map_identifier.equals(MAP_IDENTIFIER);
                if (!MAP_UP_TO_DATE) {
                    LogUtils.printErrorMessage("Map identifier mismatch. The save files will not be used.");
                    return false;
                }

                path.nodes = (Node[]) in.readObject();
                LogUtils.printErrorMessage("Node array " + path.nodes + " successfully read.");

            } catch (FileNotFoundException e) {
                LogUtils.printErrorMessage(e.getMessage());
                LogUtils.printErrorMessage("Cannot find save files!");
                return false;
            } catch (ClassNotFoundException e) {
                LogUtils.printErrorMessage(e.getMessage());
                LogUtils.printErrorMessage("Cannot find save files!");
                return false;
            } catch (IOException e) {
                LogUtils.printErrorMessage(e.getMessage());
                LogUtils.printErrorMessage("Cannot find save files!");
                return false;
            }

        }

        return MAP_UP_TO_DATE;

    }

}
