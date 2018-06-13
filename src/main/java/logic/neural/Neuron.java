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
 * Neuron.java
 * -----------------------------------------------------------------------------
 * This class is a part of a neural network used for pathfinding.
 * It is still WORK IN PROGRESS.
 * <p>
 * This class is a part of CoreNeuralNet.
 * -----------------------------------------------------------------------------
 */

package main.java.logic.neural;

import main.java.path.Intersection;
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

    }

}
