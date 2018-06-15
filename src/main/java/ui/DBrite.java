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
 * DBrite.java
 * -----------------------------------------------------------------------------
 * Work in progress class for future functions.
 * This class is not commented because it is work in progress.
 * -----------------------------------------------------------------------------
 */

package main.java.ui;

import main.java.common.LogUtils;

import java.awt.*;

public class DBrite extends FrostedPane {

    DBrite() {
        super(25, Window.window_height - 300 - 25, 300, 300, "DBRITE");

        LogUtils.printDebugMessage("Initializing DBRITE object " + this + "!");

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
