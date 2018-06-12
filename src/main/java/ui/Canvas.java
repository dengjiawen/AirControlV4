package main.java.ui;

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

    public static boolean debug;

    public static Canvas current_canvas_reference;

    public static float zoom_factor = 1f / 7f;
    public static float transpose_x;
    public static float transpose_y;

    public static float rel_mouse_point_x;
    public static float rel_mouse_point_y;

    public Canvas() {

        super();

        transpose_x = 0f;
        transpose_y = (getHeight() - ImageResource.map_YUMA_airport.getHeight() * zoom_factor) / 2;

        setBounds(0, 0,
                ImageResource.map_YUMA_airport.getWidth(),
                ImageResource.map_YUMA_airport.getHeight());

        current_canvas_reference = this;

        writeScreenForFrost();
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        RenderUtils.applyQualityRenderingHints(g2d);

        paintAll(g2d, true);

    }

    protected void paintPath(Paths path, Graphics2D g2d) {

        g2d.setColor(path.debug_color);
        g2d.setStroke(new BasicStroke(8f));
        g2d.draw(path.getPath());

    }

    protected void paintPlane(Airplane plane, Graphics2D g2d) {

        AffineTransform original = g2d.getTransform();
        g2d.rotate(plane.getHeading(), plane.getX(), plane.getY());

        g2d.drawImage(ImageResource.plane_s,
                (int) (plane.getX() - Math.sin(plane.getHeading()) * 10 - (ImageResource.plane.getWidth() * 0.1) / 2),
                (int) (plane.getY() + Math.cos(plane.getHeading()) * 3 - (ImageResource.plane.getHeight() * 0.1) / 2),
                (int) (ImageResource.plane.getWidth() * 0.1), (int) (ImageResource.plane.getHeight() * 0.1), this);

        g2d.drawImage(ImageResource.plane,
                (int) (plane.getX() - (ImageResource.plane.getWidth() * 0.1) / 2),
                (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2),
                (int) (ImageResource.plane.getWidth() * 0.1), (int) (ImageResource.plane.getHeight() * 0.1), this);

        paintLight(plane, g2d);

        if (debug) {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(5f));
            g2d.drawLine((int) plane.getX(), (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2),
                    (int) plane.getX(), (int) (plane.getY() + (ImageResource.plane.getHeight() * 0.1) / 2));

            g2d.drawOval((int) plane.getX() - 20, (int) (plane.getY() - (ImageResource.plane.getHeight() * 0.1) / 2) - 20, 40, 40);
        }

        g2d.setTransform(original);

    }

    protected void paintLight(Airplane plane, Graphics2D g2d) {

        Light light = plane.getLight();

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

        if (light.tail_strobe_on) {
            g2d.drawImage(ImageResource.white_nav,
                    (int) (plane.getX() - light.tail_strobe_offset_x + light.plane_median - ImageResource.white_nav.getWidth() * 0.4 / 2),
                    (int) (plane.getY() - light.tail_strobe_offset_y - ImageResource.white_nav.getHeight() * 0.4 / 2),
                    (int) (ImageResource.white_nav.getWidth() * 0.4), (int) (ImageResource.white_nav.getHeight() * 0.4), this);
        }

        if (light.acl_on) {
            g2d.drawImage(ImageResource.red_nav,
                    (int) (plane.getX() - light.top_acl_offset_x + light.plane_median - ImageResource.red_nav.getWidth() * 0.2 / 2),
                    (int) (plane.getY() - light.top_acl_offset_y - ImageResource.red_nav.getHeight() * 0.2 / 2),
                    (int) (ImageResource.red_nav.getWidth() * 0.2), (int) (ImageResource.red_nav.getHeight() * 0.2), this);
        }

    }

    protected void paintCloud(Graphics2D g2d) {

        CloudDirector.clouds.forEach((v, c) -> {
            g2d.drawImage(c.getSprite(), c.getX(), c.getY(), c.getWidth(), c.getHeight(), this);
        });

    }

    protected void paintAll(Graphics2D g2d, boolean haze) {

        AffineTransform transform = new AffineTransform();
        transform.scale(zoom_factor, zoom_factor);
        transform.translate(transpose_x, transpose_y);

        g2d.transform(transform);

        g2d.drawImage(ImageResource.map_YUMA_airport, 0, 0, getWidth(), getHeight(), this);

        if (debug) paintDebug(g2d);

        RefUtils.planes.forEach((i, p) -> {
            paintPlane(p, g2d);
        });

        if (haze) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, CloudDirector.haze_transparency_factor));
            g2d.setColor(Color.WHITE);

            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        paintCloud(g2d);

    }

    protected void paintDebug(Graphics2D g2d) {

        for (Paths path : Paths.values()) {
            paintPath(path, g2d);
            for (int i = 0; i < path.getNumNodes(); i++) {
                if (path.getNode(i).getType() == Node.NodeType.INTERSECTION) {
                    if (((Intersection)(path.getNode(i))).haveMustTurn()) {
                        g2d.setColor(Color.GREEN);
                    } else {
                        g2d.setColor(Color.RED);
                    }
                    g2d.fillOval((int) path.getNode(i).getX() - 10, (int) path.getNode(i).getY() - 10, 20, 20);
                } else {
                    g2d.setColor(Color.WHITE);
                    g2d.fillOval((int) path.getNode(i).getX() - 5, (int) path.getNode(i).getY() - 5, 10, 10);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(i), (int) path.getNode(i).getX() - 10, (int) path.getNode(i).getY() - 10);
            }
        }

        if (CircleUtils.active_arc != null) {
            g2d.setColor(Color.red);
            g2d.draw(CircleUtils.active_arc);
        }

        if (CircleUtils.node_collection != null) {
            CircleUtils.node_collection.forEach(e -> {
                g2d.setColor(Color.WHITE);
                g2d.fillOval((int) (e.getX() - 5), (int) (e.getY() - 5), 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.drawString("" + CircleUtils.node_collection.indexOf(e), (int) (e.getX() - 10), (int) (e.getY() - 10));
            });
        }

        CloudDirector.clouds.forEach((v, c) -> {
            g2d.setColor(Color.BLACK);
            g2d.drawString(Cloud.CloudStyle.getNameInString(c.getStyle()), c.getX(), c.getY());
        });

    }

    final static Area clipped_area = new Area(
            new Rectangle2D.Double(0, 0, Window.window_width, Window.window_height)) {{
        subtract(new Area(new Rectangle2D.Double(525, 0, 300, 756)));
    }};

    protected static void writeScreenForFrost() {

        if (!current_canvas_reference.isVisible()) return;

        ThreadUtils.frost_worker.submit(() -> {
            FrostedPane.canvas_image_buffer = new BufferedImage(Window.window_width, Window.window_height, BufferedImage.TYPE_INT_RGB);
            Graphics g = FrostedPane.canvas_image_buffer.getGraphics();
            g.setClip(clipped_area);
            current_canvas_reference.paintAll((Graphics2D) g, false);

            g.dispose();

            FrostedPane.canvas_active_image = FrostedPane.canvas_image_buffer;
            FrostedPane.current_active_panes.forEach(e -> {
                e.updateBlurImage();
            });
        });

    }

    public static void calcRelMousePoint() {
        rel_mouse_point_x = Window.mouse_point_x / zoom_factor - transpose_x;
        rel_mouse_point_y = Window.mouse_point_y / zoom_factor - transpose_y;

    }

    public static Point2D calcRelPoint(Point2D point) {

        Point2D rel_point = new Point2D.Float();

        rel_point.setLocation(point.getX() / zoom_factor - transpose_x,
                point.getY() / zoom_factor - transpose_y);

        return rel_point;

    }

}
