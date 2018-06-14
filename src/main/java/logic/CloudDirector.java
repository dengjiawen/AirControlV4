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
 * CloudDirector.java
 * -----------------------------------------------------------------------------
 * This class controls all of the Cloud objects that's currently active.
 *
 * This class is a part of AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.ui.Canvas;
import main.java.ui.RenderUtils;

import javax.swing.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CloudDirector {

    public static ConcurrentHashMap<Object, Cloud> clouds;  // hashmap of active Cloud objects

    private static Timer cloud_spawning_timer;              // timer that controls the spawn rate of clouds

    public static float haze_transparency_factor = 0;       // float that controls the transparency of hazing effects

    /**
     * Method that initializes the CloudDirector
     */
    public static void init() {

        LogUtils.printGeneralMessage("Initializing CloudDirector class!");

        /* calculate transparency of the haze */
        recalc_HazeTransparency();
        /* initiate hash map */
        clouds = new ConcurrentHashMap<>();

        int cloud_gen_percentage = Constants.getInt("cloudGenPercentage", Definitions.ZERO_DAY_PATCH_SUPPLEMENTARY);

        /* initiate cloud spawn timer */
        cloud_spawning_timer = new Timer(10000, e -> {

            /* 50% chance of generating a new random cloud */
            if (ThreadLocalRandom.current().nextInt(0, 101) > cloud_gen_percentage) return;

            Cloud new_cloud = getRandomCloud();
            if (new_cloud != null) {
                clouds.put(new Object(), new_cloud);
            }
        });

        cloud_spawning_timer.start();

    }

    /**
     * Method that generates a random cloud.
     * @return  a randomly generated cloud
     */
    private static Cloud getRandomCloud() {

        LogUtils.printGeneralMessage("CloudDirector: generating new cloud.");

        /* run through all the possible styles, randomly generate a cloud */
        for (int i = 0; i < Cloud.CloudStyle.values().length; i++) {
            if (ThreadLocalRandom.current().nextDouble(0, 2) < 0.5) {
                return Cloud.getCloudInstance(Cloud.CloudStyle.values()[i]);
            }
        }

        /* it is possible that this method will return null */
        return null;

    }


    static void removeCloud(Cloud cloud) {

        LogUtils.printGeneralMessage("CloudDirector: removing cloud " + cloud + ".");

        /* stop cloud updates and remove the cloud */
        cloud.halt();
        clouds.forEach((i, c) -> {
            if (c == cloud) {
                clouds.remove(i);
            }
        });

    }

    /**
     * Method that recalculates the transparency of the haze effect
     */
    public static void recalc_HazeTransparency() {

        /* some fancy math calculations */
        /* if zoom factor is above 1.2, remove haze completely */
        float new_transparency = -1.2f + (0.9f - Canvas.zoom_factor) * 2 + 0.1f;

        /* keep transparency between 0 and 1 */
        if (new_transparency < 0) {
            haze_transparency_factor = 0;
        } else if (new_transparency > 1) {
            haze_transparency_factor = 1;
        } else {
            haze_transparency_factor = new_transparency;
        }

        RenderUtils.invokeRepaint();

    }

}
