package main.java.logic.director;

import main.java.common.ThreadUtils;
import main.java.logic.Airplane;
import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;
import main.java.path.math.CircleUtils;
import main.java.ui.RenderUtils;

import javax.swing.*;
import java.awt.geom.Point2D;

public class TurnDirector extends Director {

    protected static final int tolerance = 1;

    private CircleUtils.TurnEvent active_event;

    private boolean recalculate_telemetry;
    private boolean reverse_after_intersection;

    private Node target_node;
    private Node current_node;

    private Paths origin;
    private Paths destination;

    private Intersection intersection;

    private double target_heading;
    private double current_heading;

    private double tick_theta;

    private double tick_x;
    private double tick_y;

    public TurnDirector(Airplane plane, Intersection intersection, Paths origin, Paths dest, boolean reverse, boolean reverse_after_intersection) {

        super(plane);

        this.active_event = new CircleUtils.TurnEvent(origin, dest, intersection, reverse, reverse_after_intersection);
        //System.out.println("New circle event created " + active_event);

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

        this.tick_update = new Timer(1000 / tick_length, e -> ThreadUtils.position_worker.submit(() -> {
            tickUpdate();
        }));

    }

    @Override
    public void tickUpdate() {

        //System.out.println("TURN DIRECTOR IS ALIVE, TARGET NODE IS: " + target_node);

        if (recalculate_telemetry) {
            recalcTelemetry(true);
        }

        plane.setPosition(
                new Point2D.Double(plane.getPosition().getX() + tick_x, plane.getPosition().getY() + tick_y));
        //System.out.println(plane.getHeading());
        plane.setHeading(plane.getHeading() + tick_theta);

        if (plane.getPosition().distance(target_node) <= tolerance) {
            plane.setPosition(target_node);
            current_node = target_node;
            recalculate_telemetry = true;
        }

        RenderUtils.invokeRepaint();

    }

    private void recalcTelemetry(boolean advance_node) {

        if (advance_node) target_node = current_node.getNextNode(false);
        //System.out.println(target_node);

        if (target_node == null) {

            //System.out.println("Switching director");

            target_node = intersection.getNextNode(destination, reverse_after_intersection);
            System.out.println(target_node);

            plane.setPosition(target_node);
            plane.setHeading(target_heading);

            plane.setActiveDirector(new TaxiDirector(plane, destination, reverse_after_intersection, target_node));

        }

        double target_time_in_seconds = current_node.distance(target_node) / (plane.getSpeed());

        if (target_time_in_seconds == 0) {
            current_node = target_node;
            recalcTelemetry(true);
            return;
        }

        tick_x = (target_node.getX() - current_node.getX()) / (target_time_in_seconds * 1000 / tick_length);
        tick_y = (target_node.getY() - current_node.getY()) / (target_time_in_seconds * 1000 / tick_length);

        //System.out.println(tick_x + ", " + tick_y + " tick");

        recalculate_telemetry = false;

    }

    private void calcTickTheta() {

        double total_distance = active_event.getTurnLength();

        //System.out.println("Arc Length: " + total_distance);

        double delta_heading = target_heading - current_heading;

        if (delta_heading < -2 * Math.PI) {
            delta_heading += 2 * Math.PI;
        } else if (delta_heading > 2 * Math.PI) {
            delta_heading -= 2 * Math.PI;
        }

        if (delta_heading > Math.PI) {
            tick_theta = - (Math.PI * 2 - delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        } else if (delta_heading < - Math.PI) {
            tick_theta = (Math.PI * 2 + delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        } else {
            tick_theta = (delta_heading) / ((total_distance / plane.getSpeed()) * 1000 / tick_length);
        }

    }

    @Override
    public void startDirector() {

        //System.out.println("Starting director...");
        tick_update.start();

    }

    @Override
    public void stopDirector() {

        tick_update.stop();
        //System.out.println("DIRECTOR STOPPED");

    }

    @Override
    public void handOff(Director new_director) {

    }


}
