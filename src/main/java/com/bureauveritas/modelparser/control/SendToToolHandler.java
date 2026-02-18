package com.bureauveritas.modelparser.control;

import burp.api.montoya.http.message.HttpRequestResponse;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.bureauveritas.modelparser.BurpApi;

import javax.swing.*;

public enum SendToToolHandler {
    REPEATER(new AbstractSendToToolHandler("Repeater") {
        @Override
        public void sendToTool(HttpRequest httpRequest, String name) {
            BurpApi.getInstance().repeater().sendToRepeater(httpRequest, name);
        }
    }),
    INTRUDER(new AbstractSendToToolHandler("Intruder") {
        @Override
        public void sendToTool(HttpRequest httpRequest, String name) {
            // Note: Intruder requires HttpService else silently fails
            // https://github.com/PortSwigger/burp-extensions-montoya-api/issues/133
            BurpApi.getInstance().intruder().sendToIntruder(httpRequest, name);
        }
    }),
    ORGANIZER(new AbstractSendToToolHandler("Organizer") {
        @Override
        public void sendToTool(HttpRequest httpRequest, String name) {
            BurpApi.getInstance().organizer().sendToOrganizer(httpRequest);
        }
    }),
    SITEMAP(new AbstractSendToToolHandler("Sitemap") {
        @Override
        public void sendToTool(HttpRequest httpRequest, String name) {
            BurpApi.getInstance().siteMap().add(HttpRequestResponse.httpRequestResponse(httpRequest, null));
        }
    }),
    SENDREQUEST(new AbstractSendToToolHandler("Send Request (see Logger)") {
        @Override
        public void sendToTool(HttpRequest httpRequest, String name) {
            // Burp blocks HTTP requests made from Swing event dispatch thread, so run on new thread
            new SwingWorker<Void,Void>() {
                @Override
                protected Void doInBackground() {
                    BurpApi.getInstance().http().sendRequest(httpRequest);
                    return null;
                }
            }.execute();
        }
    });

    private final AbstractSendToToolHandler toolItem;

    SendToToolHandler(AbstractSendToToolHandler t) {
        toolItem = t;
    }

    public void sendToTool(HttpRequest httpRequest, String name) {
        toolItem.sendToTool(httpRequest, name);
    }

    @Override
    public String toString() {
        return toolItem.toString();
    }
}
