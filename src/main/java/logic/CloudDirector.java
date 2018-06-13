package main.java.logic;

import main.java.ui.Canvas;
import main.java.ui.RenderUtils;
import main.java.ui.Window;
// Fred needs to get new priorities
import java.awt.geom.Point2D;
import javax.swing.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CloudDirector {

    public static ConcurrentHashMap<Object, Cloud> clouds;

    public static float cloud_transparency_factor;

    private static Timer cloud_spawning_timer;

    public static float haze_transparency_factor = 0;

    public static void init() {

        recalc_HazeTransparency();
        clouds = new ConcurrentHashMap<>();

        cloud_spawning_timer = new Timer(10000, e -> {
            Cloud new_cloud = getRandomCloud();
            if (new_cloud != null) {
                clouds.put(new Object(), new_cloud);
            }
        });

        cloud_spawning_timer.start();

    }

    private static Cloud getRandomCloud() {
        // Fred Dung
        for (int i = 0; i < Cloud.CloudStyle.values().length; i++) {
            if (ThreadLocalRandom.current().nextDouble(0, 2) < 0.5) {
                return Cloud.getCloudInstance(Cloud.CloudStyle.values()[i]);
            }
        }

        return null;

    }

    public static void removeCloud(Cloud cloud) {

        clouds.remove(cloud);
        cloud.halt();

    }

    public static void recalc_HazeTransparency() {

        float new_transparency = -1.2f + (0.9f - Canvas.zoom_factor) * 2 + 0.1f;
            // Fred is a dumb dumb
        if (new_transparency < 0) {
            haze_transparency_factor = 0;
        } else if (new_transparency > 1) {
            haze_transparency_factor = 1;
        } else {
            haze_transparency_factor = new_transparency;
        }

        RenderUtils.invokeRepaint();

    }

}
