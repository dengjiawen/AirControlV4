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
 * ImageResource.java
 * -----------------------------------------------------------------------------
 * This classes holds references to all of the images used in the game.
 *
 * This class is a part of CoreResource.
 * -----------------------------------------------------------------------------
 */


package main.java.resources;

import main.java.common.LogUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;

public class ImageResource {

    public static BufferedImage map_YUMA_airport;
    public static BufferedImage map_YUMA_adirs;

    public static BufferedImage plane;
    public static BufferedImage plane_s;

    public static BufferedImage white_nav;
    public static BufferedImage red_nav;
    public static BufferedImage green_nav;
    public static BufferedImage red_nav_intense;

    public static BufferedImage[] clouds;

    /**
     * Method that initializes image resources.
     */
    public static void init() {

        LogUtils.printGeneralMessage("Initializing image resources.");

        map_YUMA_airport = loadImage("/map/YUMA_airport_base.jpg");
        map_YUMA_adirs = loadImage("/map/YUMA_ADIRS.png");

        plane = loadImage("/planes/bombardier_global_7000.png");
        plane_s = loadImage("/planes/bombardier_global_7000_s.png");

        white_nav = loadImage("/luminence/white.png");
        red_nav = loadImage("/luminence/red.png");
        green_nav = loadImage("/luminence/green.png");
        red_nav_intense = loadImage("/luminence/intense_red.png");

        clouds = new BufferedImage[6];
        for (int i = 0; i < clouds.length; i++) {
            clouds[i] = loadImage("/clouds/cloud_" + i + ".png");
        }

    }

    /**
     * Method that loads image files from a given file path.
     * @param res_path file path
     * @return  loaded BufferedImage object
     */
    private static BufferedImage loadImage(String res_path) {

        try {
            LogUtils.printGeneralMessage("Attempting to load image resource " + res_path + ".");
            return ImageIO.read(ImageResource.class.getResource(res_path));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Resources are missing. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(100);
        }
        return null;
    }

}