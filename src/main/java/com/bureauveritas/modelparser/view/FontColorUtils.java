package com.bureauveritas.modelparser.view;

import com.bureauveritas.modelparser.BurpApi;

import java.awt.*;

public class FontColorUtils {
    public static float DEFAULT_LIGHTER = 0.6f;

    public static Color lighter(Color color) {
        return lighter(color, DEFAULT_LIGHTER);
    }
    public static String lighterHex(Color color) {
        return lighterHex(color, DEFAULT_LIGHTER);
    }

    public static Color lighter(Color color, float factor) {
        // Dark mode => make color darker; Light mode => make color lighter
        return switch(BurpApi.getInstance().userInterface().currentTheme()) {
            case DARK -> new Color(
                Math.max((int)(color.getRed() * 0.6), 0),
                Math.max((int)(color.getGreen() * 0.6), 0),
                Math.max((int)(color.getBlue() * 0.6), 0)
            );
            case LIGHT -> new Color(
                Math.min((int)(color.getRed() + (255 - color.getRed()) * factor), 255),
                Math.min((int)(color.getGreen() + (255 - color.getGreen()) * factor), 255),
                Math.min((int)(color.getBlue() + (255 - color.getBlue()) * factor), 255)
            );
        };
    }

    public static String lighterHex(Color color, float factor) {
        Color lighter = lighter(color, factor);
        return "#%02x%02x%02x".formatted(lighter.getRed(), lighter.getGreen(), lighter.getBlue());
    }
}
