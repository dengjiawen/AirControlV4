package main.java.ui;

import main.java.resources.ImageResource;

import java.awt.*;

public class ADIRS extends FrostedPane {

    public ADIRS() {
        super(Window.window_width - 350 - 25, 25, 350, 420, "ADIRS");

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.clipRect(0, FrostedPane.title_bar_offset, getWidth(), getHeight() - FrostedPane.title_bar_offset);
        g.setColor(Color.gray);

        Graphics2D g2d = (Graphics2D) g;

        float factor = 0.23f;

        g2d.drawImage(ImageResource.map_YUMA_adirs,
                (int) ((getWidth() - (ImageResource.map_YUMA_adirs.getWidth() * factor)) / 2),
                (int) ((getHeight() - (ImageResource.map_YUMA_adirs.getHeight() * factor)) / 2 + 10),
                (int) (ImageResource.map_YUMA_adirs.getWidth() * factor),
                (int) (ImageResource.map_YUMA_adirs.getHeight() * factor),
                this);

    }

}
