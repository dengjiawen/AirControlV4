package main.java;

import main.java.common.BlurUtils;
import main.java.common.LogUtils;
import main.java.common.ThreadUtils;
import main.java.logic.CloudDirector;
import main.java.logic.RefUtils;
import main.java.music.MusicUtils;
import main.java.path.MapUtils;
import main.java.path.Paths;
import main.java.resources.FontResource;
import main.java.resources.ImageResource;
import main.java.ui.RenderUtils;
import main.java.ui.Window;

public class LauncherTest {

    static {

        LogUtils.init();

        BlurUtils.init();

        ThreadUtils.init();
        ImageResource.init();
        FontResource.init();

        if (!Paths.readTelemetryData()) {
            System.out.println("READ FAILED");
            MapUtils.init();
            Paths.saveTelemetryData();
        }

        MusicUtils.init();
        RefUtils.init();

        CloudDirector.init();

        System.gc();
    }

    public static void main (String... args) {

        Window window = new Window();

        RenderUtils.init();

    }

}
