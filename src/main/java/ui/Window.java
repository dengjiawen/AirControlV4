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
        coord = new FrostedPane(1100, 600, 200, 70, "Coord");

        temp = new JLabel();
        temp.setForeground(Color.white);
        temp.setBounds(10, 30, 190, 40);

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
                    if (new_zoom_factor < 1f / 7f) {
                        Canvas.zoom_factor = 1f / 7f;
                    } else if (new_zoom_factor > 0.9f) {
                        Canvas.zoom_factor = 0.9f;
                    } else {
                        Canvas.zoom_factor = new_zoom_factor;
                    }

                    Canvas.calcRelMousePoint();

                    if (e.getWheelRotation() > 0) {
                        Canvas.transpose_x += Canvas.rel_mouse_point_x - original_rel_mousepoint.getX();
                        Canvas.transpose_y += Canvas.rel_mouse_point_y - original_rel_mousepoint.getY();
                    } else {

                        Point2D current_rel_midpoint = Canvas.calcRelPoint(new Point2D.Float(getWidth() / 2, getHeight() / 2));

                        Canvas.transpose_x += current_rel_midpoint.getX() - original_rel_midpoint.getX();
                        Canvas.transpose_y += current_rel_midpoint.getY() - original_rel_midpoint.getY();
                    }

                    boundCorrection();


                    RenderUtils.invokeRepaint();

                    CloudDirector.recalc_HazeTransparency();

                }

            });
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);

                requestFocus();

                initial_event = e;

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                initial_event = null;

            }
        });
        addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);

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

                if (initial_event != null) {
                    Canvas.transpose_x += (mouse_point_x - initial_event.getX()) / Canvas.zoom_factor;
                    Canvas.transpose_y += (mouse_point_y - initial_event.getY()) / Canvas.zoom_factor;
                }

                boundCorrection();

                initial_event = e;

                RenderUtils.invokeRepaint();
            }

        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    SpeechUtils.startSpeechSession();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
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

    private void boundCorrection() {

        Point2D rel_point = Canvas.calcRelPoint(new Point2D.Float(0f, 0f));

        if (rel_point.getX() < 0f) {
            Canvas.transpose_x += (rel_point.getX());
        }

        if (rel_point.getY() < 0f) {
            Canvas.transpose_y += (rel_point.getY());
        }

        rel_point = Canvas.calcRelPoint(new Point2D.Float(getWidth(), getHeight()));

        if (rel_point.getX() > ImageResource.map_YUMA_airport.getWidth()) {
            Canvas.transpose_x -= ImageResource.map_YUMA_airport.getWidth() - rel_point.getX();
        }

        if (rel_point.getY() > ImageResource.map_YUMA_airport.getHeight()) {
            Canvas.transpose_y -= ImageResource.map_YUMA_airport.getHeight() - rel_point.getY();
        }

    }

    static void invokeRepaint() {
        current_window_reference.revalidate();
        current_window_reference.repaint();
    }

}
