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
 * Cloud.java
 * -----------------------------------------------------------------------------
 * This is a class containing the Cloud object.
 * <p>
 * This class is a part of the AssistLogic.
 * -----------------------------------------------------------------------------
 */

package main.java.logic;

import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.resources.ImageResource;
import main.java.ui.Canvas;
import main.java.ui.RenderUtils;
import main.java.ui.Window;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import javax.swing.Timer;
import java.util.concurrent.ThreadLocalRandom;

public class Cloud {

    /**
     * Static block to initiate Director constants
     */
    static {

        cloud_tick = Constants.getInt("tickLength", Definitions.ZERO_DAY_PATCH_SUPPLEMENTARY);

    }

    private static int cloud_tick;      // tick length of cloud update timers

    private BufferedImage cloud_sprite; // image of the cloud
    private float resize_factor;        // float to resize the image to proper size

    @Deprecated
    private Rectangle bound;    // deprecated member variable

    private CloudStyle style;   // the style of the cloud

    private Point2D position;           // position of the cloud
    private Timer cloud_tick_update;    // timer that updates the cloud location

    private Cloud(BufferedImage sprite, float factor, CloudStyle style, Rectangle bound, Point2D initial_pos) {

        LogUtils.printGeneralMessage("New Cloud object initiated: " + this + "!");

        /* initialize instance variables */
        this.cloud_sprite = sprite;
        this.resize_factor = factor;
        this.position = initial_pos;

        this.style = style;
        this.bound = bound;

        this.cloud_tick_update = new Timer(1000 / cloud_tick, e -> {

            // move cloud up by 1 unit
            position.setLocation(position.getX(), position.getY() - 1);

            // if cloud is out of bound, remove cloud
            if (position.getY() < -500) CloudDirector.removeCloud(this);

            // invoke repaint
            RenderUtils.invokeRepaint();

        });

    }

    /**
     * Method that returns the x pos of the cloud.
     * @return  int, x pos
     */
    public int getX() {

        return (int) position.getX();

    }

    /**
     * Method that returns the y pos of the cloud.
     * @return  int, y pos
     */
    public int getY() {

        return (int) position.getY();

    }

    /**
     * Method that returns the image of the cloud.
     * @return  BufferedImage, image
     */
    public BufferedImage getSprite() {

        return cloud_sprite;

    }

    /**
     * Method that returns the height of the cloud.
     * @return  int, height
     */
    public int getHeight() {

        return (int) (getSprite().getHeight() * resize_factor);

    }

    /**
     * Method that returns the width of the cloud.
     * @return  int, width
     */
    public int getWidth() {

        return (int) (getSprite().getWidth() * resize_factor);

    }

    /**
     * Method that initializes the cloud object.
     */
    public void init() {

        cloud_tick_update.start();

        LogUtils.printGeneralMessage("Cloud object initiated: " + this + ".");

    }

    /**
     * Method that returns the style of the cloud.
     * @return CloudStyle enum
     */
    public CloudStyle getStyle() {

        return style;

    }

    /**
     * Method that stops the cloud update timer.
     */
    public void halt() {

        cloud_tick_update.stop();

        LogUtils.printGeneralMessage("Cloud object halted: " + this + ".");

    }

    /**
     * Static method that initializes a cloud using a particular cloud style.
     * @param style the desired cloud style
     * @return  the created cloud
     */
    public static Cloud getCloudInstance(CloudStyle style) {

        Cloud new_cloud = new Cloud(style.cloud.cloud_sprite, style.cloud.resize_factor,
                style.cloud.style, style.cloud.bound, style.generateRandomPosition());
        new_cloud.init();

        LogUtils.printGeneralMessage("New cloud instance created from style " + style + ": " + new_cloud + "!");

        return new_cloud;

    }

    /**
     * Enumeration of different cloud styles, with its own images and resize factors
     */
    public enum CloudStyle {

        CLOUD_0(ImageResource.clouds[0], 0.5f), CLOUD_1(ImageResource.clouds[1], 1.5f),
        CLOUD_2(ImageResource.clouds[2], 0.5f), CLOUD_3(ImageResource.clouds[3], 1f),
        CLOUD_4(ImageResource.clouds[4], 1f), CLOUD_5(ImageResource.clouds[5], 0.5f);

        private Cloud cloud;    // a reference Cloud object for the cloud style

        /**
         * Enum constructor
         * @param sprite    the image of the cloud
         * @param factor    the resizing factor of the cloud
         */
        CloudStyle(BufferedImage sprite, float factor) {
            cloud = new Cloud(sprite, factor, this, null, null);
        }

        /**
         * Method that generates a random position for a given cloud style,
         * used for the randomization of cloud spawning.
         * @return  Point2D of a random position.
         */
        private Point2D generateRandomPosition() {

            LogUtils.printGeneralMessage("Random position requested for cloud style " + this + ".");

            return new Point2D.Double(
                    ThreadLocalRandom.current().nextDouble(0, ImageResource.map_YUMA_airport.getWidth() + 1),
                    ImageResource.map_YUMA_airport.getHeight() + cloud.cloud_sprite.getHeight() * cloud.resize_factor);

        }

        /**
         * Method that returns the name of the cloud style.
         * @param style the target style
         * @return  string, name of cloud style
         */
        public static String getNameInString(CloudStyle style) {

            return style.name();

        }

    }

}
