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
 * Airplane.java
 * -----------------------------------------------------------------------------
 * This is the airplane object. It dictates the plane's speed, position,
 * and angle.
 * <p>
 * This class is a part of CoreLogic and inherits the Director ADT.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.logic.director.Director;
import main.java.logic.director.TaxiDirector;

import main.java.path.Paths;

import java.awt.geom.Point2D;

public class Airplane {

    private Point2D position;   // plane's position

    private double speed;           // plane's speed
    private double current_heading; // plane's current angle

    private Director active_director;   // plane's active director
    private Light light;                // plane's light controllers

    /**
     * Constructor
     * @param path  the starting path of the plane
     */
    Airplane(Paths path) {

        LogUtils.printGeneralMessage("New Airplane object initiated: " + this + "!");

        /* initialize instance variables */
        this.position = new Point2D.Double(path.getNode(0).getX(), path.getNode(0).getY());

        this.speed = Constants.getInt("testSpeed", Definitions.ZERO_DAY_PATCH);

        this.active_director = new TaxiDirector(this, path, false, 0);
        this.active_director.startDirector();

        this.light = new Light();

    }

    /**
     * Method that returns the plane heading.
     * @return  double, heading
     */
    public double getHeading() {
        return current_heading;
    }

    /**
     * Method that sets the plane heading.
     * @param heading   double, heading
     */
    public void setHeading(double heading) {
        current_heading = heading;
    }

    /**
     * Method that returns the plane position.
     * @return  Point2D, position
     */
    public Point2D getPosition() {
        return position;
    }

    /**
     * Method that sets the plane position.
     * @param position  Point2D, position
     */
    public void setPosition(Point2D position) {
        this.position.setLocation(position);
    }

    /**
     * Sets a new active director.
     * @param director  Director ADT, new director
     */
    public void setActiveDirector(Director director) {

        /* stop old director, hand off and start new director */
        active_director.stopDirector();
        active_director.handOff(director);
        active_director = director;
        active_director.startDirector();
    }

    public double getSpeed() {
        return speed;
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public Light getLight() {

        return light;

    }

    public void instructToTurnAtPath (Paths path, boolean reverse_after_intersection) {

        TaxiDirector director;
        try {
            director = (TaxiDirector) active_director;
        } catch (ClassCastException e) {
            //e.printStackTrace();
            return;
        }

        director.setTargetPath(path, reverse_after_intersection);

    }

}
