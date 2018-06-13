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
 * BasicQueue.java
 * -----------------------------------------------------------------------------
 * This class is a part of a neural network used for pathfinding.
 * It is still WORK IN PROGRESS.
 * <p>
 * This class is a part of CoreNeuralNet.
 * -----------------------------------------------------------------------------
 */

package main.java.logic.neural;


import java.util.LinkedList;

public class BasicQueue<E> {

    private LinkedList<E> list = new LinkedList<E>();

    public void put(E o) {
        list.addLast(o);
    }

    public E get() {
        if (list.isEmpty()) {
            return null;
        }
        return list.removeFirst();
    }

    public Object[] getAll() {
        Object[] res = new Object[list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = list.get(i);
        }
        list.clear();
        return res;
    }

    public E peek() {
        return list.getFirst();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int size() {
        return list.size();
    }

}