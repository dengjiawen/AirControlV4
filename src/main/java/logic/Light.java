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
 * Light.java
 * -----------------------------------------------------------------------------
 * This class contains a Light object that controls all aspects of the
 * Airplane's lighting.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.ui.RenderUtils;

import javax.swing.*;

public class Light {

    /* these booleans control various lighting */
    public boolean strobe_on;
    public byte strobe_count;

    public boolean tail_strobe_on;
    public byte tail_strobe_count;

    public boolean nav_on;
    public boolean acl_on;

    /* these parameter defines the position of the light */
    public int wingtip_strobe_offset_x;
    public int wingtip_strobe_offset_y;

    public int wingtip_nav_offset_x;
    public int wingtip_nav_offset_y;

    public int plane_median;

    public int tail_strobe_offset_x;
    public int tail_strobe_offset_y;

    public int top_acl_offset_x;
    public int top_acl_offset_y;

    /* these timer controls the strobe lighting */
    private Timer acl_light_regulator;

    private Timer strobe_regulator;
    private Timer strobe_rest_regulator;

    private Timer tail_strobe_regulator;
    private Timer tail_strobe_rest_regulator;

    /**
     * Default constructor
     */
    Light() {

        LogUtils.printGeneralMessage("New Light object " + this + " created!");

        /* initialize controller timers */
        acl_light_regulator = new Timer(300, e -> {

            acl_on = !acl_on;
            acl_light_regulator.setDelay(
                    (acl_light_regulator.getDelay() == 300) ? 1500 : 300);

            RenderUtils.invokeRepaint();

        });
        strobe_regulator = new Timer(200, e -> {

            if (strobe_count == 0) {
                strobe_on = true;
                strobe_count++;
            } else if (strobe_count == 1) {
                strobe_on = false;
                strobe_count++;
            } else if (strobe_count == 2) {
                strobe_on = true;
                strobe_count++;
            } else if (strobe_count == 3) {
                strobe_on = false;
                strobe_regulator.stop();
                strobe_rest_regulator.restart();
            }

            RenderUtils.invokeRepaint();

        });
        strobe_rest_regulator = new Timer(1000, e -> {
            if (strobe_count != 0) {
                strobe_count = 0;
            } else if (strobe_count == 0) {
                strobe_rest_regulator.stop();
                strobe_regulator.restart();
            }
        });

        tail_strobe_regulator = new Timer(250, e -> {

            if (tail_strobe_count == 0) {
                tail_strobe_on = true;
                tail_strobe_count++;
            } else if (tail_strobe_count == 1) {
                tail_strobe_on = false;
                tail_strobe_count++;
            } else if (tail_strobe_count == 2) {
                tail_strobe_on = true;
                tail_strobe_count++;
            } else if (tail_strobe_count == 3) {
                tail_strobe_on = false;
                tail_strobe_regulator.stop();
                tail_strobe_rest_regulator.restart();
            }

            RenderUtils.invokeRepaint();

        });
        tail_strobe_rest_regulator = new Timer(1250, e -> {
            if (tail_strobe_count != 0) {
                tail_strobe_count = 0;
            } else if (tail_strobe_count == 0) {
                tail_strobe_rest_regulator.stop();
                tail_strobe_regulator.restart();
            }
        });

        /* initialize position variables */
        init();

        /* turn all light on */
        setAll(true);

    }

    /**
     * Method that loads all of the position parameters.
     */
    public void init() {

        wingtip_nav_offset_x = Constants.getInt("wingTipOffsetX", Definitions.BOMBARDIER_PATCH);
        wingtip_nav_offset_y = Constants.getInt("wingTipOffsetY", Definitions.BOMBARDIER_PATCH);

        wingtip_strobe_offset_x = Constants.getInt("wingTipStrobeOffsetX", Definitions.BOMBARDIER_PATCH);
        wingtip_strobe_offset_y = Constants.getInt("WingTipStrobeOffsetY", Definitions.BOMBARDIER_PATCH);

        tail_strobe_offset_x = Constants.getInt("tailStrobeOffsetX", Definitions.BOMBARDIER_PATCH);
        tail_strobe_offset_y = Constants.getInt("tailStrobeOffsetY", Definitions.BOMBARDIER_PATCH);

        top_acl_offset_x = Constants.getInt("topACLOffsetX", Definitions.BOMBARDIER_PATCH);
        top_acl_offset_y = Constants.getInt("topACLOffsetY", Definitions.BOMBARDIER_PATCH);

        plane_median = Constants.getInt("planeMedian", Definitions.BOMBARDIER_PATCH);

    }

    /**
     * The following methods toggle various lights on/off.
     * @param on    whether the light is on (true = on, false = off)
     */

    private void setACL(boolean on) {

        LogUtils.printGeneralMessage("Light object " + this + " signaling ACL = " + on + "!");
        if (on) acl_light_regulator.restart();
        else acl_light_regulator.stop();

    }

    private void setStrobe(boolean on) {

        LogUtils.printGeneralMessage("Light object " + this + " signaling Strobe = " + on + "!");
        if (on) {
            strobe_regulator.restart();
            tail_strobe_regulator.restart();
        } else {
            strobe_regulator.stop();
            tail_strobe_regulator.stop();
        }

    }

    private void setNAV(boolean on) {

        LogUtils.printGeneralMessage("Light object " + this + " signaling NAV = " + on + "!");
        nav_on = on;

    }

    public void setAll(boolean on) {

        LogUtils.printGeneralMessage("Light object " + this + " signaling All Lights = " + on + "!");

        setStrobe(on);
        setACL(on);
        setNAV(on);

        RenderUtils.invokeRepaint();

    }

}
