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
 * CommandUtils.java
 * -----------------------------------------------------------------------------
 * This classes processes incoming commands from the GUI.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.path.Paths;
import main.java.ui.Canvas;
import main.java.ui.RenderUtils;

public class CommandUtils {

    /**
     * This class converts String command into text.
     * @param command
     */
    public static void TEST_CODE(String command) {

        if (command.toUpperCase().contains("DEBUG ON")) {
            Canvas.debug = true;
            RenderUtils.invokeRepaint();
        } else if (command.toUpperCase().contains("DEBUG OFF")) {
            Canvas.debug = false;
            RenderUtils.invokeRepaint();
        }

        Paths mentioned_path = searchPath(command);
        if (mentioned_path != null) {

            if (command.toUpperCase().contains("TURN")) {
                boolean reverse_after_intersection = command.toUpperCase().contains("REVERSE");
                RefUtils.planes.get(RefUtils.current_index_planes - 1).instructToTurnAtPath(mentioned_path, reverse_after_intersection);
                RenderUtils.invokeRepaint();
            } else if (command.toUpperCase().contains("RESPAWN")) {
                RefUtils.respawn(mentioned_path);
            }

        }

    }

    private static Paths searchPath(String command) {

        for (Paths path : Paths.values()) {
            if (command.toUpperCase().contains(path.getName())) {
                return path;
            }
        }

        return null;

    }

}
