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
 * Window.java
 * -----------------------------------------------------------------------------
 * This object is the main JFrame for this program. It hosts the Canvas panel.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.common.LogUtils;
import main.java.common.ThreadUtils;
import main.java.constants.Constants;
import main.java.constants.Definitions;
import main.java.logic.CloudDirector;
import main.java.resources.ImageResource;
import main.java.speech.SpeechUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class Window extends JFrame {

    /* a reference of the active window */
    private static Window current_window_reference;

    static float mouse_point_x;     // x pos of the mouse cursor
    static float mouse_point_y;     // y pos of the mouse cursor

    /* window size constants */
    static int window_height = Constants.getInt("WindowHeight", Definitions.UI_CONSTANTS);
    static int window_width = Constants.getInt("WindowWidth", Definitions.UI_CONSTANTS);

    /* initial mouse event; when the mouse first starts to drag */
    private static MouseEvent initial_event;

    private Canvas canvas;          // canvas: graphics painting panel
    private CommandPanel command;   // command: for user to enter command
    private DBrite dbrite;          // WORK IN PROGRESS
    private ADIRS adirs;            // WORK IN PROGRESS
    private FrostedPane coord;      // coordinate panel: for debug purposes only
    private JLabel temp;            // for displaying coordinate

    /**
     * Default constructor
     */
    public Window() {
        super();

        LogUtils.printGeneralMessage("Initializing main program Window " + this + ".");

        /* initialize JFrame */
        setSize(window_width, window_height);
        setUndecorated(true);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

        /* initialize custom JComponents */
        canvas = new Canvas();
        current_window_reference = this;

        command = new CommandPanel();
        adirs = new ADIRS();
        dbrite = new DBrite();
        coord = new FrostedPane(
                Constants.getInt("CoordPanelX", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordPanelY", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordPanelW", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordPanelH", Definitions.DEBUG_PATCH),
                "Coord");

        temp = new JLabel();
        temp.setForeground(Color.white);
        temp.setBounds(
                Constants.getInt("CoordTextX", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordTextY", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordTextW", Definitions.DEBUG_PATCH),
                Constants.getInt("CoordTextH", Definitions.DEBUG_PATCH));

        coord.add(temp);

        /* mouse listener for zooming */
        addMouseWheelListener(e -> {

            requestFocus();

            /* submit calculations to mouse worker thread */
            ThreadUtils.mouse_worker.submit(() -> {

                /* get the relative midpoint of the current screen section */
                Point2D original_rel_midpoint = Canvas.calcRelPoint(new Point2D.Float(getWidth() / 2, getHeight() / 2));

                /* update the relative location of the mouse on the screen;
                 * so that the screen zoom towards the cursor */
                Canvas.calcRelMousePoint();


                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

                    Point2D original_rel_mousepoint = new Point2D.Float(
                            Canvas.rel_mouse_point_x, Canvas.rel_mouse_point_y);

                    /* recalculate zoom factor based on wheel rotation */
                    float new_zoom_factor = Canvas.zoom_factor + e.getWheelRotation() / 50f;

                    /* ensure that zoom factor is between 1/7 and 0.9. (factors are purely arbitrary */
                    if (new_zoom_factor < 1f / 7f) {
                        Canvas.zoom_factor = 1f / 7f;
                    } else if (new_zoom_factor > 0.9f) {
                        Canvas.zoom_factor = 0.9f;
                    } else {
                        Canvas.zoom_factor = new_zoom_factor;
                    }

                    /* recalcualte relative mouse point */
                    Canvas.calcRelMousePoint();

                    /* position Canvas object to ensure that mouse cursor remains in original position */
                    if (e.getWheelRotation() > 0) {
                        Canvas.transpose_x += Canvas.rel_mouse_point_x - original_rel_mousepoint.getX();
                        Canvas.transpose_y += Canvas.rel_mouse_point_y - original_rel_mousepoint.getY();
                    } else {

                        /* place mouse cursor in the middle of the screen */
                        Point2D current_rel_midpoint = Canvas.calcRelPoint(new Point2D.Float(getWidth() / 2, getHeight() / 2));

                        Canvas.transpose_x += current_rel_midpoint.getX() - original_rel_midpoint.getX();
                        Canvas.transpose_y += current_rel_midpoint.getY() - original_rel_midpoint.getY();
                    }

                    /* correct Canvas bound */
                    boundCorrection();

                    RenderUtils.invokeRepaint();

                    /* recalculate haze transparency based on new zoom factor */
                    CloudDirector.recalc_HazeTransparency();

                }

            });
        });
        /* mouse listener for tracking dragging events */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                requestFocus();

                /* keep reference of initial mouse event when mouse is pressed */
                initial_event = e;

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                /* nullify initial event when mouse is released */
                initial_event = null;

            }
        });
        /* mouse listener for tracking dragging events */
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

                /* whenever mouse move, calculate its relative position on the screen */
                /* update this position for the coord. panel (for debugging purposes only) */
                mouse_point_x = e.getX();
                mouse_point_y = e.getY();

                Canvas.calcRelMousePoint();

                temp.setText("X: " + (int)Canvas.rel_mouse_point_x + ", Y: " + (int)Canvas.rel_mouse_point_y);

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                mouseMoved(e);

                requestFocus();

                /* move the image in correspondence with the mouse movement */
                if (initial_event != null) {
                    Canvas.transpose_x += (mouse_point_x - initial_event.getX()) / Canvas.zoom_factor;
                    Canvas.transpose_y += (mouse_point_y - initial_event.getY()) / Canvas.zoom_factor;
                }

                /* correct bound */
                boundCorrection();

                initial_event = e;

                RenderUtils.invokeRepaint();
            }

        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                /* if shift is pressed, begin speech recognition session */
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

                    LogUtils.printDebugMessage("SpeechUtils: (BETA) Speech session began.");
                    SpeechUtils.startSpeechSession();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                /* if shift is released, stop speech recognition session */
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {

                    LogUtils.printDebugMessage("SpeechUtils: (BETA) Speech session stopped.");
                    SpeechUtils.stopSpeechSession();
                }
            }
        });

        add(command);
        add(dbrite);
        add(adirs);
        add(coord);
        add(canvas);

        setVisible(true);

        RenderUtils.invokeRepaint();

        requestFocus();

    }

    /**
     * This method checks if Canvas is out of bounds, and ensures
     * that Canvas stays within the visible range of the Window
     * object at all times.
     */
    private void boundCorrection() {

        /* get relative point of 0,0 */
        Point2D rel_point = Canvas.calcRelPoint(new Point2D.Float(0f, 0f));

        /* if canvas had moved to the left of the Window, move it to the right */
        if (rel_point.getX() < 0f) {
            Canvas.transpose_x += (rel_point.getX());
        }

        /* if canvas had moved to the top of the Window, move it down */
        if (rel_point.getY() < 0f) {
            Canvas.transpose_y += (rel_point.getY());
        }

        /* get relative point of getWidth, getHeight (window size) */
        rel_point = Canvas.calcRelPoint(new Point2D.Float(getWidth(), getHeight()));

        /* if the canvas had moved to the right of the Window, move it to the left */
        if (rel_point.getX() > ImageResource.map_YUMA_airport.getWidth()) {
            Canvas.transpose_x -= ImageResource.map_YUMA_airport.getWidth() - rel_point.getX();
        }

        /* if the canvas had moved to the bottom of the window, move it up */
        if (rel_point.getY() > ImageResource.map_YUMA_airport.getHeight()) {
            Canvas.transpose_y -= ImageResource.map_YUMA_airport.getHeight() - rel_point.getY();
        }

    }

    /**
     * Method that revalidates and repaints all JComponents hosted within Window.
     */
    static void invokeRepaint() {
        current_window_reference.revalidate();
        current_window_reference.repaint();
    }

}
