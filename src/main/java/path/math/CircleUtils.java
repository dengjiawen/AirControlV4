package main.java.path.math;

import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;

import java.awt.geom.*;
import java.util.ArrayList;

public class CircleUtils {

    public static Arc2D active_arc;
    public static ArrayList<TurningNodes> node_collection;

    public static class TurningNodes extends Node {

        private TurnEvent event;

        public TurningNodes(Point2D p, TurnEvent event) {
            super(p, null, null);
            this.event = event;
        }

        public Node getNextNode(boolean reverse) {
            return event.getNextNode(this);
        }

    }

    public static class TurnEvent {

        private double radius;

        private double target_heading;
        private double current_heading;
        private double delta_theta;

        private boolean reverse_after_turning;
        private boolean reverse;

        private Paths origin;
        private Paths dest;

        private Intersection intersection;
        private Arc2D arc;

        private PathIterator iterator;
        private ArrayList<TurningNodes> turning_nodes;

        public TurnEvent(Paths origin, Paths dest, Intersection intersection, boolean reverse, boolean reverse_after_turning) {

            this.origin = origin;
            this.dest = dest;
            this.intersection = intersection;

            this.current_heading = origin.getHeading(reverse);
            this.target_heading = dest.getHeading(reverse_after_turning);

            this.turning_nodes = new ArrayList<>();

            this.reverse = reverse;
            this.reverse_after_turning = reverse_after_turning;

            calcDeltaTheta();
            calcRadius();

            this.arc = new Arc2D.Double(Arc2D.OPEN);
            this.arc.setArcByTangent(intersection.getJumpPoint(origin, !reverse),
                    intersection, intersection.getJumpPoint(dest, reverse_after_turning), radius);

            this.iterator = new FlatteningPathIterator(arc.getPathIterator(null), 1f);
            iteratePath();

            //TODO: DELETE
            active_arc = this.arc;
            node_collection = turning_nodes;

        }

        private void iteratePath() {

            double[] coords = new double[6];

            for (PathIterator i = iterator; !i.isDone(); i.next()) {

                i.currentSegment(coords);
                turning_nodes.add(new TurningNodes(new Point2D.Double(coords[0], coords[1]), this));

            }

        } // Hi there. :D

        public void calcDeltaTheta() {

            delta_theta = getDifference(target_heading, current_heading);

//            delta_theta = target_heading - current_heading;
//
//            if (delta_theta < -2 * Math.PI) delta_theta += 2 * Math.PI;
//            else if (delta_theta > 2 * Math.PI) delta_theta -= 2 * Math.PI;
//
//            if (delta_theta < -Math.PI) delta_theta += Math.PI;
//            else if (delta_theta > Math.PI) delta_theta -= Math.PI;

        }

        public void calcRadius() {

            Node origin_node = intersection.getJumpPoint(origin, !reverse);
            Node dest_node = intersection.getJumpPoint(dest, reverse_after_turning);

//            System.out.println("Target Angle: " + target_heading);
//            System.out.println("Current Angle: " + current_heading);
//            System.out.println("Delta Theta: " + delta_theta);

            double partial_theta = delta_theta / 2;
            double sin_partial_theta = Math.sin(partial_theta);
            radius = 1 / (sin_partial_theta / (origin_node.distance(dest_node) / 2));

            if (radius < 0) radius = -radius;

        }

        public TurningNodes getNextNode(TurningNodes node) {

            try {
                return turning_nodes.get(
                        turning_nodes.indexOf(node) + 1
                );
            } catch (IndexOutOfBoundsException e) {
                return null;
            }

        }

        public TurningNodes getFirstNode() {
            return turning_nodes.get(0);
        }

        public double getTurnLength() {

            double total_distance = 0;
            for (int i = 0; i < turning_nodes.size() - 1; i++) {
                total_distance += turning_nodes.get(i).distance(turning_nodes.get(i + 1));
            }

            return total_distance;

        }
    }

    static double getDifference(double a1, double a2) {
        return Math.min((a1 - a2) < 0 ? a1 - a2 + 360 : a1 - a2, (a2 - a1) < 0 ? a2 - a1 + 360 : a2 - a1);
    }

}
