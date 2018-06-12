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
 * BlurUtils.java
 * -----------------------------------------------------------------------------
 * This is a specialized java class designed to process imagery information,
 * applying a rough gaussian blur effect to the images using multiple
 * threads. (Be aware of thread conflicts)
 * <p>
 * This class is a part of the CoreGraphics, and is essential for the
 * normal functions of this software.
 * <p>
 * This class should not be changed under any circumstances.
 * -----------------------------------------------------------------------------
 */

package main.java.common;

import main.java.constants.Constants;
import main.java.constants.Definitions;

import java.awt.Color;
import java.awt.image.BufferedImage;

import java.util.ArrayList;

public class BlurUtils {

    /**
     * Blur Filter, Level 9
     */
    private static final int[][] filter9 = {{1, 1, 1},
            {1, 1, 1},
            {1, 1, 1}
    };

    /**
     * Blur Filter, Level 16
     */
    private static final int[][] filter16 = {{1, 2, 1},
            {2, 4, 2},
            {1, 2, 1}
    };

    private static int filter_sum;     // the sum of the filter values
    private static int iteration_num;  // the number of iterations on image operations (blur intensity)

    private static int[][] filter;     // the chosen filter (as defined in CoreDefinitions.constants)

    /**
     * Method that initializes variables within BlurUtils.
     */
    public static void init() {

        LogUtils.printGeneralMessage("Initializing BlurUtils...");

        iteration_num = Constants.getInt("iterationNumber", Definitions.CORE_CONSTANTS);
        int filter_type = Constants.getInt("filterType", Definitions.CORE_CONSTANTS);
        switch (filter_type) {
            case 16:
                filter = filter16;
                break;
            case 9:
                filter = filter9;
                break;
            default:
                filter = filter16;
        }

        filter_sum = sum();

        LogUtils.printGeneralMessage("BlurUtils successfully initiated!");

    }

    /**
     * Method that takes an image and applies the blur
     * effect to it.
     *
     * @param image         target image
     * @return bufferedimage, blurred
     */
    public BufferedImage getFilteredImage(BufferedImage image) {

        LogUtils.printRepaintMessage("Blurring operation on image " + image.toString() + " started.");

        /* use arraylist for its parallel stream multithread processing */
        ArrayList<Integer> y = new ArrayList<Integer>() {{
            for (int y = 1; y + 1 < image.getHeight(); y++) {
                add(y);
            }
        }};

        ArrayList<Integer> x = new ArrayList<Integer>() {{
            for (int x = 1; x + 1 < image.getWidth(); x++) {
                add(x);
            }
        }};

        /* use parallel stream to process every pixel in the image
           simultaneously using getFilteredValue() */
        int count = 0;
        while (count < iteration_num) {

            y.parallelStream().forEach(y1 ->
                    x.parallelStream().forEach(x1 -> {
                        Color tempColor = getFilteredValue(image, y1, x1);
                        image.setRGB(x1, y1, tempColor.getRGB());
                    }));

            /* iterate more times for more blurred effects */
            count++;

        }

        LogUtils.printRepaintMessage("Blurring operation on image " + image.toString() + " completed.");

        return image;
    }

    /**
     * Method that computes the RGB value for individual
     * pixels in order to apply the blur effects.
     *
     * @param image  the target image
     * @param y      pixel coord, y
     * @param x      pixel coord, x
     * @return color, the recalculated RBG value of the pixel
     */
    private Color getFilteredValue(final BufferedImage image, int y, int x) {
        int r = 0, g = 0, b = 0;
        for (int j = -1; j <= 1; j++) {
            for (int k = -1; k <= 1; k++) {

                /* recalculate the RGB value for the given pixel on the image */
                r += (filter[1 + j][1 + k] * (new Color(image.getRGB(x + k, y + j))).getRed());
                g += (filter[1 + j][1 + k] * (new Color(image.getRGB(x + k, y + j))).getGreen());
                b += (filter[1 + j][1 + k] * (new Color(image.getRGB(x + k, y + j))).getBlue());
            }

        }

        r = r / filter_sum;
        g = g / filter_sum;
        b = b / filter_sum;

        return new Color(r, g, b);
    }

    /**
     * Method that retrieves the sum of the given filter.
     *
     * @return sum of filter values
     */
    private static int sum() {

        /* add up all of the numbers in the filter array */
        int sum = 0;
        for (int[] filter_segment : filter) {
            for (int filter_subsegment : filter_segment) {
                sum += filter_subsegment;
            }
        }

        return sum;
    }
}