

package main.java.ui;

import main.java.common.BlurUtils;
import main.java.resources.FontResource;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FrostedPane extends JPanel {

    static ArrayList<FrostedPane> current_active_panes = new ArrayList<>();

    static BufferedImage canvas_image_buffer;
    static BufferedImage canvas_active_image;

    static int title_bar_offset = 30;

    ExecutorService blur_daemon;
    BufferedImage blurred_image;

    String name;

    public FrostedPane(int x, int y, int width, int height, String name) {
        super();

        setLayout(null);
        setBounds(x, y, width, height);

        blur_daemon = Executors.newCachedThreadPool();
        blurred_image = null;

        current_active_panes.add(this);

        this.name = name;

        RenderUtils.invokeRepaint();
    }

    protected void updateBlurImage() {
        blur_daemon.submit(() -> {

            BufferedImage active_image_buffer = canvas_active_image.getSubimage(getX(), getY(), getWidth(), getHeight());

            active_image_buffer = new BlurUtils().getFilteredImage(active_image_buffer);

            blurred_image = active_image_buffer;

            SwingUtilities.invokeLater(() -> repaint());
        });
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (blurred_image != null) {

            try {
                g.drawImage(blurred_image, 0, 0, this);
            } catch (RasterFormatException e) {
                //ignore
            }

            Graphics2D g2d = (Graphics2D) g;

            RenderUtils.applyQualityRenderingHints(g2d);

            g2d.setComposite(AlphaComposite.SrcOver.derive(0.7f));
            g2d.setColor(Color.black);
            g2d.fillRect(0, title_bar_offset, getWidth(), getHeight() - title_bar_offset);

            g2d.setComposite(AlphaComposite.SrcOver.derive(0.9f));
            g2d.fillRect(0, 0, getWidth(), title_bar_offset);

            g2d.setFont(FontResource.window_title);
            g2d.setColor(Color.white);
            g2d.drawString(name, 10, 20);

        }

    }

}
