package com.bureauveritas.modelparser.view.component;

import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.text.ParseException;

public class PortNumberFormatter extends NumberFormatter {
    public PortNumberFormatter() {
        super(NumberFormat.getIntegerInstance());
        setMinimum(0);
        setMaximum(65535);
        ((NumberFormat) this.getFormat()).setGroupingUsed(false);
        setAllowsInvalid(false);
        setCommitsOnValidEdit(true);
    }

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (text == null || text.trim().isEmpty()) {
            return null; // Allow empty field
        }
        return super.stringToValue(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value == null) {
            return ""; // Display empty string for null
        }
        return super.valueToString(value);
    }
}
