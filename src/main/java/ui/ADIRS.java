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
 * ADIRS.java
 * -----------------------------------------------------------------------------
 * Work in progress class for future functions.
 * This class is not commented because it is work in progress.
 * -----------------------------------------------------------------------------
 */

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
