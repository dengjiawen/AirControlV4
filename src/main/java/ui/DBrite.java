package main.java.ui;

import java.awt.*;

public class DBrite extends FrostedPane {

    public DBrite() {
        super(25, Window.window_height - 300 - 25, 300, 300, "DBRITE");

    }

    protected void paintComponent (Graphics g) {
        super.paintComponent(g);

        g.clipRect(0, FrostedPane.title_bar_offset, getWidth(), getHeight() - FrostedPane.title_bar_offset);
        g.setColor(Color.gray);

        Graphics2D g2d = (Graphics2D) g;

        g2d.drawOval((getWidth() - 80) / 2, (getHeight() - 80 + FrostedPane.title_bar_offset) / 2, 80, 80);
        g2d.drawOval((getWidth() - 160) / 2, (getHeight() - 160 + FrostedPane.title_bar_offset) / 2, 160, 160);
        g2d.drawOval((getWidth() - 240) / 2, (getHeight() - 240 + FrostedPane.title_bar_offset) / 2, 240, 240);

    }

}
