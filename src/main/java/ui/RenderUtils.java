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
 * RenderUtils.java
 * -----------------------------------------------------------------------------
 * This class manages all repaint events for animations being painted
 * in the Canvas object.
 * -----------------------------------------------------------------------------
 * This is a legacy class ported from the TeslaUI project. Compatibility is
 * not guaranteed.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.constants.Constants;
import main.java.constants.Definitions;

import javax.swing.*;
import java.awt.*;

public class RenderUtils {

    private static boolean do_repaint = false;       // whether a repaint should be triggered

    /**
     * Method that initializes the RenderUtils class.
     */
    public static void init() {

        /* timer that repaints based on a pre-set refresh rate */
        Timer rendering_daemon = new Timer(1000 / Constants.getInt
                ("RefreshRate", Definitions.CORE_CONSTANTS), e -> {

            /* if repaint is requested, invoke repaint and update
             * blurred image effect for FrostedPanes */
            if (do_repaint) {
                SwingUtilities.invokeLater(() -> {
                    Window.invokeRepaint();
                    Canvas.writeScreenForFrost();
                });

                do_repaint = false;
            }
        });

        rendering_daemon.start();
    }

    /**
     * Tells the RenderUtils to repaint by setting doRepaint to true
     */
    public static void invokeRepaint() {
        do_repaint = true;
    }

    /**
     * Method that applies rendering hint to Graphics objects in order
     * to improve rendering quality.
     * @param g2d
     */
    static void applyQualityRenderingHints(Graphics2D g2d) {

        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

    }

}
