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
 * FontResource.java
 * -----------------------------------------------------------------------------
 * This classes holds references to all of the custom fonts used in the game.
 *
 * This class is a part of CoreResource.
 * -----------------------------------------------------------------------------
 */

package main.java.resources;

import main.java.common.LogUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class FontResource {

    private static final String font_directory = "/fonts/";

    private static Font regular;
    private static Font italics;

    private static Font medium;
    private static Font medium_italics;

    private static Font bold;
    private static Font bold_italics;

    public static Font window_title;

    public static Font command_content;
    public static Font command_hint;

    /**
     * Method that initializes font resources.
     */
    public static void init() {

        LogUtils.printGeneralMessage("Initializing font resources.");

        regular = loadFont("regular");
        italics = loadFont("italics");

        medium = loadFont("medium");
        medium_italics = loadFont("medium_italics");

        bold = loadFont("bold");
        bold_italics = loadFont("bold_italics");

        window_title = bold.deriveFont(11f);
        command_content = regular.deriveFont(11f);
        command_hint = italics.deriveFont(11f);

    }

    /**
     * Method that loads font files from a given file path.
     * @param font_name file path
     * @return  loaded Font object
     */
    private static Font loadFont(String font_name) {

        try {
            LogUtils.printGeneralMessage("Attempting to load custom font " + font_name + ".");
            return Font.createFont(Font.TRUETYPE_FONT, FontResource.class.getResourceAsStream(font_directory + font_name + ".ttf"));
        } catch (FontFormatException e) {
            LogUtils.printErrorMessage(e.getMessage());
            JOptionPane.showMessageDialog(null, "Resources are missing. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(100);
        } catch (IOException e) {
            LogUtils.printErrorMessage(e.getMessage());
            JOptionPane.showMessageDialog(null, "Resources are missing. " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(100);
        }

        return null;

    }

}
