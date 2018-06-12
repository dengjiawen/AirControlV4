package main.java.logic;

import main.java.logic.director.Director;
import main.java.logic.director.TaxiDirector;
import main.java.path.Paths;

import java.awt.geom.Point2D;

public class Airplane {

    private Point2D position;

    private double speed;
    private double current_heading;

    Director active_director;
    Light light;

    public Airplane(Paths path) {

        this.position = new Point2D.Double(path.getNode(0).getX(), path.getNode(0).getY());

        this.speed = 50f;

        active_director = new TaxiDirector(this, path, false, 0);
        active_director.startDirector();

        light = new Light();

    }

    public double getHeading() {
        return current_heading;
    }

    public void setHeading(double heading) {
        current_heading = heading;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position.setLocation(position);
    }

    public void setActiveDirector(Director director) {
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

        System.out.println("Received, " + path);

    }

}
