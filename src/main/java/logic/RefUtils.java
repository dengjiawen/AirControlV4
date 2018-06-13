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
 * RefUtils.java
 * -----------------------------------------------------------------------------
 * This classes holds reference to all of the Airplane objects.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.common.LogUtils;
import main.java.path.Paths;
import main.java.ui.RenderUtils;

import java.util.concurrent.ConcurrentHashMap;

public class RefUtils {

    public static ConcurrentHashMap<Integer, Airplane> planes;
    static int current_index_planes = 0;

    /**
     * Method that initializes the references.
     */
    public static void init() {

        planes = new ConcurrentHashMap<>();

        planes.put(current_index_planes++, new Airplane(Paths.taxiE));

        LogUtils.printGeneralMessage("RefUtils successfully initiated!");

    }

    /**
     * Method that respawns the plane at a different path.
     * @param target_path
     */
    public static void respawn(Paths target_path) {

        planes.get(current_index_planes - 1).disablePlane();
        LogUtils.printGeneralMessage("Airplane object " + planes.get(current_index_planes - 1) + " is about to be removed.");
        planes.remove(current_index_planes - 1);

        planes.put(current_index_planes++, new Airplane(target_path));
        LogUtils.printGeneralMessage("Airplane object " + planes.get(current_index_planes - 1) + " had been added!");

        RenderUtils.invokeRepaint();

    }

}
