package com.bureauveritas.modelparser.view.component;

import javax.swing.*;

public class JTableImmutable extends JTable {
    @Override
    public boolean isCellEditable(int row, int column) {
        return false; // Disable table editing
    }
}
