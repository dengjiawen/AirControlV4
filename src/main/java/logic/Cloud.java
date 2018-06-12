package main.java.logic;

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

    private BufferedImage cloud_sprite;
    private float resize_factor;

    private Rectangle bound;
    private CloudStyle style;

    private Point2D position;
    private Timer cloud_tick_update;

    private Cloud(BufferedImage sprite, float factor, CloudStyle style, Rectangle bound, Point2D initial_pos) {

        cloud_sprite = sprite;
        resize_factor = factor;
        position = initial_pos;

        this.style = style;
        this.bound = bound;

        cloud_tick_update = new Timer(1000/50, e -> {

            position.setLocation(position.getX(), position.getY() - 1);

            if (position.getY() < -500) CloudDirector.removeCloud(this);

            RenderUtils.invokeRepaint();

        });

    }

    public int getX() {

        return (int) position.getX();

    }

    public int getY() {

        return (int) position.getY();

    }

    public BufferedImage getSprite() {

        return cloud_sprite;

    }

    public int getHeight() {

        return (int)(getSprite().getHeight() * resize_factor);

    }

    public int getWidth() {

        return (int)(getSprite().getWidth() * resize_factor);

    }

    public void init() {

        cloud_tick_update.start();

    }

    public CloudStyle getStyle() {

        return style;

    }

    public void halt() {
        cloud_tick_update.stop();
    }

    public static Cloud getCloudInstance(CloudStyle style) {

        Cloud new_cloud = new Cloud(style.cloud.cloud_sprite, style.cloud.resize_factor,
                style.cloud.style, style.cloud.bound, style.generateRandomPosition());
        new_cloud.init();

        return new_cloud;

    }

    public enum CloudStyle {

        CLOUD_0(ImageResource.clouds[0], 0.5f), CLOUD_1(ImageResource.clouds[1], 1.5f),
        CLOUD_2(ImageResource.clouds[2], 0.5f), CLOUD_3(ImageResource.clouds[3], 1f),
        CLOUD_4(ImageResource.clouds[4], 1f), CLOUD_5(ImageResource.clouds[5], 0.5f);

        private Cloud cloud;

        CloudStyle(BufferedImage sprite, float factor) {
            cloud = new Cloud(sprite, factor, this, null, null);
        }

        private Point2D generateRandomPosition() {

            return new Point2D.Double(
                    ThreadLocalRandom.current().nextDouble(0, ImageResource.map_YUMA_airport.getWidth() + 1),
                    ImageResource.map_YUMA_airport.getHeight() + cloud.cloud_sprite.getHeight() * cloud.resize_factor);

        }

        public static String getNameInString(CloudStyle style) {

            return style.name();

        }

    }

}
