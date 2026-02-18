package com.bureauveritas.modelparser.view.component;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EnterKeyAdapter extends KeyAdapter {
    private final JButton button;

    public EnterKeyAdapter(JButton btn) {
        super();
        button = btn;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            button.doClick();
        }
    }
}
