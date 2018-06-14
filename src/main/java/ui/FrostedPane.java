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
 * FrostedPane.java
 * -----------------------------------------------------------------------------
 * This custom JComponent blurs any content painted underneath it on the
 * canvas.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.common.BlurUtils;
import main.java.common.LogUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.resources.FontResource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrostedPane extends JPanel {

    static ArrayList<FrostedPane> current_active_panes = new ArrayList<>(); // reference of active FrostedPanes for updates

    static BufferedImage canvas_image_buffer;   // buffer of the image being calculated
    static BufferedImage canvas_active_image;   // active image being shown

    static int title_bar_offset = Constants.getInt("titleOffset", Definitions.UI_CONSTANTS);    // offset of the panel title

    private ExecutorService blur_daemon;    // Thread for handling the blurring operation
    private BufferedImage blurred_image;    // The current image being calcualted

    String name;    // the name shown on the panel

    /**
     * Constructor
     * @param x         x pos of the panel
     * @param y         y pos of the panel
     * @param width     panel width
     * @param height    panel height
     * @param name      panel title
     */
    FrostedPane(int x, int y, int width, int height, String name) {

        super();

        LogUtils.printGeneralMessage("Initializing new FrostedPane " + this + ".");

        /* nullify layout */
        setLayout(null);
        setBounds(x, y, width, height);

        /* initialize instance variables */
        blur_daemon = Executors.newCachedThreadPool();
        blurred_image = null;

        current_active_panes.add(this);

        this.name = name;

        RenderUtils.invokeRepaint();

    }

    /**
     * Method that updates the background image of the JPanel
     */
    void updateBlurImage() {

        blur_daemon.submit(() -> {

            /* get the section of image occupied by the pane */
            /* apply blurring operation */
            BufferedImage active_image_buffer = canvas_active_image.getSubimage(getX(), getY(), getWidth(), getHeight());
            active_image_buffer = new BlurUtils().getFilteredImage(active_image_buffer);

            blurred_image = active_image_buffer;

            SwingUtilities.invokeLater(() -> repaint());

        });


    }

    /**
     * Overloaded paintComponent method
     * @param g
     */
    @ Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (blurred_image != null) {

            Graphics2D g2d = (Graphics2D) g;

            /* apply antialiasing and other rendering effects etc */
            RenderUtils.applyQualityRenderingHints(g2d);

            /* draw out blurred image */
            try {
                g2d.drawImage(blurred_image, 0, 0, this);
            } catch (RasterFormatException e) {
                LogUtils.printErrorMessage(e.getMessage());
            }

            /* set transparency for black window material */
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f));
            g2d.setColor(Color.black);
            g2d.fillRect(0, title_bar_offset, getWidth(), getHeight() - title_bar_offset);

            /* set transparency for black window title material */
            g2d.setComposite(AlphaComposite.SrcOver.derive(0.9f));
            g2d.fillRect(0, 0, getWidth(), title_bar_offset);

            /* draw window title String */
            g2d.setFont(FontResource.window_title);
            g2d.setColor(Color.white);
            g2d.drawString(name, 10, 20);

        }

    }

}
