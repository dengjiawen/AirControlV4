package main.java.ui;

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

    private static Window current_window_reference;

    public static float mouse_point_x;
    public static float mouse_point_y;

    public static int window_height = Constants.getInt("WindowHeight", Definitions.UI_CONSTANTS);
    public static int window_width = Constants.getInt("WindowWidth", Definitions.UI_CONSTANTS);

    private static MouseEvent initial_event;

    private Canvas canvas;
    private CommandPanel command;
    private DBrite dbrite;
    private ADIRS adirs;
    private FrostedPane coord;
    private JLabel temp;

    public Window() {
        super();

        setSize(
                Constants.getInt("WindowWidth", Definitions.UI_CONSTANTS),
                Constants.getInt("WindowHeight", Definitions.UI_CONSTANTS));
        setUndecorated(true);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);

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

        addMouseWheelListener(e -> {

            requestFocus();

            ThreadUtils.mouse_worker.submit(() -> {

                Point2D original_rel_midpoint = Canvas.calcRelPoint(new Point2D.Float(getWidth() / 2, getHeight() / 2));

                Canvas.calcRelMousePoint();

                if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

                    Point2D original_rel_mousepoint = new Point2D.Float(
                            Canvas.rel_mouse_point_x, Canvas.rel_mouse_point_y);

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

                    //System.out.println(Canvas.zoom_factor);
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

    void boundCorrection() {

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
