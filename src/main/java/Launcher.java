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
 * Launcher.java
 * -----------------------------------------------------------------------------
 * This class is the entry point for the program.
 * -----------------------------------------------------------------------------
 */

package main.java;

import main.java.common.BlurUtils;
import main.java.common.LogUtils;
import main.java.common.ThreadUtils;
import main.java.logic.CloudDirector;
import main.java.logic.RefUtils;
import main.java.music.MusicUtils;
import main.java.path.MapUtils;
import main.java.path.Paths;
import main.java.resources.FontResource;
import main.java.resources.ImageResource;
import main.java.ui.RenderUtils;
import main.java.ui.Window;

public class Launcher {

    /**
     * Static block that initiates all of the different classes.
     */
    static {

        LogUtils.init();

        LogUtils.printCoreMessage("Initializing launcher...");

        BlurUtils.init();

        ThreadUtils.init();
        ImageResource.init();
        FontResource.init();

        /* try to read data written by Paths */
        /* if reading is not successful, tell MapUtil to recalculate paths */
        LogUtils.printCoreMessage("Launcher is trying to read saved telemetry data...");
        if (!Paths.readTelemetryData()) {
            LogUtils.printCoreMessage("Launcher cannot read saved telemetry data! Will recalculate.");
            MapUtils.init();
            Paths.saveTelemetryData();
        }

        MusicUtils.init();
        RefUtils.init();

        CloudDirector.init();

        LogUtils.printCoreMessage("Launcher is requesting garbage collection...");
        System.gc();
    }

    /**
     * Main method
     * Creates a new Window object, and initializes
     * the RenderUtils class.
     * @param args
     */
    public static void main (String... args) {

        LogUtils.printCoreMessage("Launcher is initializing the main window.");
        Window window = new Window();

        RenderUtils.init();

    }

}
