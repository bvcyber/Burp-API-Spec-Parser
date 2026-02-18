package com.bureauveritas.modelparser.view;

import com.bureauveritas.modelparser.BurpApi;

import java.awt.*;

public class EDTEventQueueLogger extends EventQueue {
    @Override
    protected void dispatchEvent(AWTEvent event) {
        try {
            super.dispatchEvent(event);
        }
        catch (Throwable t) {
            // Log uncaught EDT exceptions
            BurpApi.getInstance().logging().logToError(t);
            throw t;
        }
    }
}
