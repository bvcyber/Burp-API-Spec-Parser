package com.bureauveritas.modelparser.view.render;

import javax.swing.table.DefaultTableCellRenderer;

public class HtmlTableCellRenderer extends DefaultTableCellRenderer {
    public HtmlTableCellRenderer() {
        super();
        putClientProperty("html.disable", Boolean.FALSE);
    }
}
