package main.java.resources;

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

    public static void init() {

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

    private static Font loadFont(String font_name) {

        try {
            return Font.createFont(Font.TRUETYPE_FONT, FontResource.class.getResourceAsStream(font_directory + font_name + ".ttf"));
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

}
