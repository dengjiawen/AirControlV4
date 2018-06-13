package main.java.logic;

import main.java.ui.RenderUtils;

import javax.swing.*;

public class Light {

    public boolean strobe_on;
    public byte strobe_count;

    public boolean tail_strobe_on;
    public byte tail_strobe_count;

    public boolean nav_on;
    public boolean acl_on;

    public int wingtip_strobe_offset_x;
    public int wingtip_strobe_offset_y;

    public int wingtip_nav_offset_x;
    public int wingtip_nav_offset_y;

    public int plane_median;

    public int tail_strobe_offset_x;
    public int tail_strobe_offset_y;

    public int top_acl_offset_x;
    public int top_acl_offset_y;

    private Timer acl_light_regulator;

    private Timer strobe_regulator;
    private Timer strobe_rest_regulator;

    private Timer tail_strobe_regulator;
    private Timer tail_strobe_rest_regulator;

    public Light() {

        acl_light_regulator = new Timer(300, e -> {

            acl_on = !acl_on;
            acl_light_regulator.setDelay(
                    (acl_light_regulator.getDelay() == 300) ? 1500 : 300);

            RenderUtils.invokeRepaint();

        });
        strobe_regulator = new Timer(200, e -> {

            if (strobe_count == 0) {
                strobe_on = true;
                strobe_count++;
            } else if (strobe_count == 1) {
                strobe_on = false;
                strobe_count++;
            } else if (strobe_count == 2) {
                strobe_on = true;
                strobe_count++;
            } else if (strobe_count == 3) {
                strobe_on = false;
                strobe_regulator.stop();
                strobe_rest_regulator.restart();
            }

            RenderUtils.invokeRepaint();

        });
        strobe_rest_regulator = new Timer(1000, e -> {
            if (strobe_count != 0) {
                strobe_count = 0;
            } else if (strobe_count == 0) {
                strobe_rest_regulator.stop();
                strobe_regulator.restart();
            }
        });

        tail_strobe_regulator = new Timer(250, e -> {

            if (tail_strobe_count == 0) {
                tail_strobe_on = true;
                tail_strobe_count++;
            } else if (tail_strobe_count == 1) {
                tail_strobe_on = false;
                tail_strobe_count++;
            } else if (tail_strobe_count == 2) {
                tail_strobe_on = true;
                tail_strobe_count++;
            } else if (tail_strobe_count == 3) {
                tail_strobe_on = false;
                tail_strobe_regulator.stop();
                tail_strobe_rest_regulator.restart();
            }

            RenderUtils.invokeRepaint();

        });
        tail_strobe_rest_regulator = new Timer(1250, e -> {
            if (tail_strobe_count != 0) {
                tail_strobe_count = 0;
            } else if (tail_strobe_count == 0) {
                tail_strobe_rest_regulator.stop();
                tail_strobe_regulator.restart();
            }
        });

        init();

        setAll(true);

    }

    public void init() {

        wingtip_nav_offset_x = 55;
        wingtip_nav_offset_y = -27;

        wingtip_strobe_offset_x = 55;
        wingtip_strobe_offset_y = -22;

        tail_strobe_offset_x = 0;
        tail_strobe_offset_y = -58;

        top_acl_offset_x = 0;
        top_acl_offset_y = 40;

        plane_median = 0;

    }

    public void setACL(boolean on) {

        if (on) acl_light_regulator.restart();
        else acl_light_regulator.stop();

    }

    public void setStrobe(boolean on) {

        if (on) {
            strobe_regulator.restart();
            tail_strobe_regulator.restart();
        } else {
            strobe_regulator.stop();
            tail_strobe_regulator.stop();
        }

    }

    public void setNAV(boolean on) {

        nav_on = on;

    }

    public void setAll(boolean on) {

        setStrobe(on);
        setACL(on);
        setNAV(on);

        RenderUtils.invokeRepaint();

    }

}
