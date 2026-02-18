package com.bureauveritas.modelparser.view.component;

import com.bureauveritas.modelparser.view.FontColorUtils;

import javax.swing.*;
import java.awt.*;

public class JTextFieldWithPlaceholder extends JTextField {
    private final String placeholder;

    public JTextFieldWithPlaceholder(String placeholder) {
        super();
        this.placeholder = placeholder;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // https://stackoverflow.com/questions/16213836/java-swing-jtextfield-set-placeholder
        super.paintComponent(g);

        if (getText().isEmpty()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(FontColorUtils.lighter(UIManager.getColor("TextField.foreground")));
            g2.setFont(getFont());

            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(placeholder, x, y);
            g2.dispose();
        }
    }
}
