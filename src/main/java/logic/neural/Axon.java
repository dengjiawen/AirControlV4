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
 * Axon.java
 * -----------------------------------------------------------------------------
 * This class is a part of a neural network used for pathfinding.
 * It is still WORK IN PROGRESS.
 * <p>
 * This class is a part of CoreNeuralNet.
 * -----------------------------------------------------------------------------
 */

package main.java.logic.neural;

import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;
import main.java.path.math.LinearUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Axon {

    Paths init_path;
    Paths dest_path;
    Paths curr_path;

    boolean reverse;

    Node init_node;

    Queue<Neuron> neurons = new ConcurrentLinkedQueue<>();

    public Axon(Paths init_path, Paths dest_path, boolean reverse, Node init_node) {

        this.init_path = init_path;
        this.dest_path = dest_path;
        this.curr_path = init_path;

        this.reverse = reverse;



    }

    private void initialize_recursive(Node init_node) {

        BasicQueue<Intersection> sorted_intersections = sortByRandomizer(generateRandomizers());

        Intersection target_intersection = sorted_intersections.peek();
        if (target_intersection.getNumPaths() > 1 && target_intersection.containsPath(init_path)) {

        }

        neurons.add(new Neuron(curr_path, (Intersection)init_node, null, null, false, this));

    }

    private BasicQueue<Intersection> sortByRandomizer(HashMap<Double, Intersection> randomizers) {

        ArrayList<Double> numbers = new ArrayList<Double>(){{
            randomizers.forEach((k, v) -> add(k));
        }};

        BasicQueue<Intersection> queue = new BasicQueue<>();

        numbers = LinearUtils.mergeSort(numbers);
        numbers.forEach(n -> System.out.println(n));

        for (int i = 0; i < numbers.size(); i++) {
            queue.put(randomizers.get(numbers.get(i)));
        }

        return queue;

    }

    private HashMap<Double, Intersection> generateRandomizers() {

        ArrayList<Intersection> intersections = findNextIntersections();
        ArrayList<Double> random_numbers = generateRandomNumbers(intersections.size());

        HashMap<Double, Intersection> randomizers = new HashMap<>();

        for (int i = 0; i < intersections.size(); i ++) {
            randomizers.put(random_numbers.get(i), intersections.get(i));
        }

        return randomizers;

    }

    private ArrayList<Double> generateRandomNumbers(int number) {

        ArrayList<Double> random_numbers = new ArrayList<>();

        for (int i = 0; i < number; i ++) {
            random_numbers.add(Math.random());
        }

        return random_numbers;

    }

    private ArrayList<Intersection> findNextIntersections() {

        int intersection_index = init_path.getNodeIndex(init_node);
        ArrayList<Intersection> intersections = new ArrayList<>();

        if (!reverse) {

            for (int i = intersection_index; i < init_path.getNumNodes(); i ++) {
                if (init_path.getNode(i).getType() == Node.NodeType.INTERSECTION) {
                    intersections.add((Intersection)init_path.getNode(i));
                }
            }

        } else {

            for (int i = intersection_index; i > 0; i --) {
                if (init_path.getNode(i).getType() == Node.NodeType.INTERSECTION) {
                    intersections.add((Intersection)init_path.getNode(i));
                }
            }

        }

        return intersections;

    }

}
