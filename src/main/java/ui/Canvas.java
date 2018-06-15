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
 * Canvas.java
 * -----------------------------------------------------------------------------
 * This panel paints all of the Graphics object onto the window.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.common.LogUtils;
import main.java.common.ThreadUtils;
import main.java.logic.*;
import main.java.path.Intersection;
import main.java.path.Node;
import main.java.path.Paths;
import main.java.path.math.CircleUtils;
import main.java.resources.ImageResource;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Canvas extends JPanel {

    public static boolean debug;    // whether debug mode should be enabled

    private static Canvas current_canvas_reference; // static reference of the currently active Canvas object

    public static float zoom_factor = 1f / 7f;  // zoom factor
    static float transpose_x;                   // canvas displacement in Window, x
    static float transpose_y;                   // canvas displacement in Window, y

    static float rel_mouse_point_x;     // relative mouse position on canvas, x
    static float rel_mouse_point_y;     // relative mouse position on canvas, y

    /**
     * Default constructor
     */
    Canvas() {

        super();

        LogUtils.printGeneralMessage("Initializing Canvas " + this + "!");

        /* initialize instance variables */
        transpose_x = 0f;
        transpose_y = (getHeight() - ImageResource.map_YUMA_airport.getHeight() * zoom_factor) / 2;

        setBounds(0, 0,
                ImageResource.map_YUMA_airport.getWidth(),
                ImageResource.map_YUMA_airport.getHeight());

        current_canvas_reference = this;

        writeScreenForFrost();

    }

    /**
     * Overriden paintComponent method.
     * @param g
     */
    @ Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        RenderUtils.applyQualityRenderingHints(g2d);

        paintAll(g2d, true);

    }

    /**
     * Method that paints a given path for debugging purposes.
     * @param path
     * @param g2d
     */
    private void paintPath(Paths path, Graphics2D g2d) {

        /* paint path with 8f stroke */
        g2d.setColor(path.debug_color);
        g2d.setStroke(new BasicStroke(8f));
        g2d.draw(path.getPath());

    }

    /**
     * Method that paints a given plane.
     * @param plane
     * @param g2d
     */
    private void paintPlane(Airplane plane, Graphics2D g2d) {

        AffineTransform original = g2d.getTransform();

        /* rotate g2d object to plane heading */
        g2d.rotate(plane.getHeading(), plane.getX(), plane.getY());

        /* draw plane shadow, compensating for shadow consistency */
        g2d.drawImage(ImageResource.plane_s,
                (int) (plane.getX() - Math.sin(plane.getHeading()) * 10 - (ImageResource.plane.getWidth() * 0.1) / 2),
                (int) (plane.getY() + Math.cos(plane.getHeading()) * 3 - (ImageResource.plane.getHeight() * 0.1) / 2),
                (int) (ImageResource.plane.getWidth() * 0.1), (int) (ImageResource.plane.getHeight() * 0.1), this);

        /* draw plane at the correct location */
        g2d.drawImage(ImageResource.plane,
                (int) (plane.getX() - (ImageResource.plane.getWidth() * 0.1) / 2),
                (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2),
                (int) (ImageResource.plane.getWidth() * 0.1), (int) (ImageResource.plane.getHeight() * 0.1), this);

        /* draw lights for the plane */
        paintLight(plane, g2d);

        /* draw a line and a circle to highlight the plane in debug mode */
        if (debug) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5f));
            g2d.drawLine((int) plane.getX(), (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2),
                    (int) plane.getX(), (int) (plane.getY() + (ImageResource.plane.getHeight() * 0.1) / 2));

            g2d.drawOval((int) plane.getX() - 20, (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2) - 20, 40, 40);
        }

        g2d.setTransform(original);

    }

    /**
     * Method that paints lighting for a given plane.
     * @param plane
     * @param g2d
     */
    private void paintLight(Airplane plane, Graphics2D g2d) {

        /* grab light reference from plane */
        Light light = plane.getLight();

        /* paint the navigation (weak red and green wingtip light) effect at the proper location */
        if (light.nav_on) {
            g2d.drawImage(ImageResource.red_nav,
                    (int) (plane.getX() - light.wingtip_nav_offset_x + light.plane_median - ImageResource.red_nav.getWidth() * 0.2 / 2),
                    (int) (plane.getY() - light.wingtip_nav_offset_y - ImageResource.red_nav.getHeight() * 0.2 / 2),
                    (int) (ImageResource.red_nav.getWidth() * 0.2), (int) (ImageResource.red_nav.getHeight() * 0.2), this);
            g2d.drawImage(ImageResource.green_nav,
                    (int) (plane.getX() + light.wingtip_nav_offset_x + light.plane_median - ImageResource.green_nav.getWidth() * 0.2 / 2),
                    (int) (plane.getY() - light.wingtip_nav_offset_y - ImageResource.green_nav.getHeight() * 0.2 / 2),
                    (int) (ImageResource.green_nav.getWidth() * 0.2), (int) (ImageResource.green_nav.getHeight() * 0.2), this);
        }

        /* paint the wingtip strobe light effect at the proper location */
        if (light.strobe_on) {
            g2d.drawImage(ImageResource.white_nav,
                    (int) (plane.getX() - light.wingtip_strobe_offset_x + light.plane_median - ImageResource.white_nav.getWidth() * 0.3 / 2),
                    (int) (plane.getY() - light.wingtip_strobe_offset_y - ImageResource.white_nav.getHeight() * 0.3 / 2),
                    (int) (ImageResource.white_nav.getWidth() * 0.3), (int) (ImageResource.white_nav.getHeight() * 0.3), this);
            g2d.drawImage(ImageResource.white_nav,
                    (int) (plane.getX() + light.wingtip_strobe_offset_x + light.plane_median - ImageResource.white_nav.getWidth() * 0.3 / 2),
                    (int) (plane.getY() - light.wingtip_strobe_offset_y - ImageResource.white_nav.getWidth() * 0.3 / 2),
                    (int) (ImageResource.white_nav.getWidth() * 0.3), (int) (ImageResource.white_nav.getHeight() * 0.3), this);
        }

        /* paint the tail strobe light effect at the proper location */
        if (light.tail_strobe_on) {
            g2d.drawImage(ImageResource.white_nav,
                    (int) (plane.getX() - light.tail_strobe_offset_x + light.plane_median - ImageResource.white_nav.getWidth() * 0.4 / 2),
                    (int) (plane.getY() - light.tail_strobe_offset_y - ImageResource.white_nav.getHeight() * 0.4 / 2),
                    (int) (ImageResource.white_nav.getWidth() * 0.4), (int) (ImageResource.white_nav.getHeight() * 0.4), this);
        }

        /* paint the anti-collision warning strobe light effect at the proper location */
        if (light.acl_on) {
            g2d.drawImage(ImageResource.red_nav,
                    (int) (plane.getX() - light.top_acl_offset_x + light.plane_median - ImageResource.red_nav.getWidth() * 0.2 / 2),
                    (int) (plane.getY() - light.top_acl_offset_y - ImageResource.red_nav.getHeight() * 0.2 / 2),
                    (int) (ImageResource.red_nav.getWidth() * 0.2), (int) (ImageResource.red_nav.getHeight() * 0.2), this);
        }

    }

    /**
     * Method that paints all of the cloud objects.
     * @param g2d
     */
    private void paintCloud(Graphics2D g2d) {

        /* iterate through cloud objects and draw the correct sprite at the correct location */
        CloudDirector.clouds.forEach((v, c) -> {
            g2d.drawImage(c.getSprite(), c.getX(), c.getY(), c.getWidth(), c.getHeight(), this);
        });

    }

    /**
     * Master paint method, paints all of the graphical elements.
     * @param g2d
     * @param haze  whether the hazing effect should be painted
     *              (haze is not painted when painting for BlurUtils)
     */
    private void paintAll(Graphics2D g2d, boolean haze) {

        /* transform g2d by zoom factor and transposition */
        AffineTransform transform = new AffineTransform();
        transform.scale(zoom_factor, zoom_factor);
        transform.translate(transpose_x, transpose_y);

        g2d.transform(transform);

        /* draw airport image as background */
        g2d.drawImage(ImageResource.map_YUMA_airport, 0, 0, getWidth(), getHeight(), this);

        /* if debug is needed, paint debug contents */
        if (debug) paintDebug(g2d);

        /* iterate through planes and paint each plane */
        RefUtils.planes.forEach((i, p) -> {
            paintPlane(p, g2d);
        });

        /* if haze is being painted, set transparency and fill with white color */
        if (haze) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, CloudDirector.haze_transparency_factor));
            g2d.setColor(Color.WHITE);

            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        /* restore transparency to 100% and paint all of the clouds */
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        paintCloud(g2d);

    }

    /**
     * Method that paints debugging contents.
     * @param g2d
     */
    private void paintDebug(Graphics2D g2d) {

        /* paint all of the paths */
        for (Paths path : Paths.values()) {
            paintPath(path, g2d);

            /* highlight intersections with green (if the plane must turn) and red */
            for (int i = 0; i < path.getNumNodes(); i++) {
                if (path.getNode(i).getType() == Node.NodeType.INTERSECTION) {
                    if (((Intersection)(path.getNode(i))).haveMustTurn()) {
                        g2d.setColor(Color.GREEN);
                    } else {
                        g2d.setColor(Color.RED);
                    }
                    g2d.fillOval((int) path.getNode(i).getX() - 10, (int) path.getNode(i).getY() - 10, 20, 20);
                } else {

                    /* highlight other nodes with white */
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval((int) path.getNode(i).getX() - 5, (int) path.getNode(i).getY() - 5, 10, 10);
                }

                /* number the nodes */
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i), (int) path.getNode(i).getX() - 10, (int) path.getNode(i).getY() - 10);
            }
        }

        /* highlight the active turning arc with red */
        if (CircleUtils.active_arc != null) {
            g2d.setColor(Color.red);
            g2d.draw(CircleUtils.active_arc);
        }

        /* highlight active TurningNodes with white */
        if (CircleUtils.node_collection != null) {
            CircleUtils.node_collection.forEach(e -> {
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int) (e.getX() - 5), (int) (e.getY() - 5), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("" + CircleUtils.node_collection.indexOf(e), (int) (e.getX() - 10), (int) (e.getY() - 10));
            });
        }

        /* show the style of each of the clouds */
        CloudDirector.clouds.forEach((v, c) -> {
            g2d.setColor(Color.BLACK);
            g2d.drawString(Cloud.CloudStyle.getNameInString(c.getStyle()), c.getX(), c.getY());
        });

    }

    /* this Area object bounds the area being painted for blurring */
    private final static Area clipped_area = new Area(
            new Rectangle2D.Double(0, 0, Window.window_width, Window.window_height)) {{
        subtract(new Area(new Rectangle2D.Double(525, 0, 300, 756)));
    }};

    /**
     * This method paints all the content on canvas to be blurred.
     */
    static void writeScreenForFrost() {

        if (!current_canvas_reference.isVisible()) return;

        /* toss the damn task to the workers */
        ThreadUtils.frost_worker.submit(() -> {

            /* create new buffer image */
            FrostedPane.canvas_image_buffer = new BufferedImage(Window.window_width, Window.window_height, BufferedImage.TYPE_INT_RGB);

            /* paint canvas content onto the buffer, without haze */
            Graphics g = FrostedPane.canvas_image_buffer.getGraphics();
            g.setClip(clipped_area);
            current_canvas_reference.paintAll((Graphics2D) g, false);

            g.dispose();

            /* blur image and send to active FrostedPanes */
            FrostedPane.canvas_active_image = FrostedPane.canvas_image_buffer;
            FrostedPane.current_active_panes.forEach(e -> {
                e.updateBlurImage();
            });
        });

    }

    /**
     * Method that calculates the relative mouse point on the Canvas,
     * by taking into account zoom factor and transpose factors.
     */
    static void calcRelMousePoint() {
        rel_mouse_point_x = Window.mouse_point_x / zoom_factor - transpose_x;
        rel_mouse_point_y = Window.mouse_point_y / zoom_factor - transpose_y;

    }

    /**
     * Method that calculates a relative point on the Canvas.
     * This method operates on the same principle as calcRelMousePoint().
     * @param point point on Window
     * @return  relative point on Canvas
     */
    static Point2D calcRelPoint(Point2D point) {

        Point2D rel_point = new Point2D.Float();

        rel_point.setLocation(point.getX() / zoom_factor - transpose_x,
                point.getY() / zoom_factor - transpose_y);

        return rel_point;

    }

}
