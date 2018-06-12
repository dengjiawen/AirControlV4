package main.java.logic.neural;


import main.java.path.Intersection;// Hi
import main.java.path.Paths;

public class Neuron {

    Paths init_path;
    Intersection init_intersection;

    Paths dest_path;
    Intersection dest_intersection;

    Axon parent_axon;

    boolean reverse;

    public Neuron(Paths init_path, Intersection init_intersection, Paths dest_path, Intersection dest_intersection, boolean reverse, Axon parent_axon) {

        this.init_path = init_path;
        this.init_intersection = init_intersection;
        this.parent_axon = parent_axon;

        this.reverse = reverse;
            // 3=======================================================================D  Is this yours, Fred?
    }

}
