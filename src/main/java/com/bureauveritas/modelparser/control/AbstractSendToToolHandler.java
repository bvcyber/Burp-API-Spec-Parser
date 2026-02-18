package com.bureauveritas.modelparser.control;

import burp.api.montoya.http.message.requests.HttpRequest;

abstract class AbstractSendToToolHandler {
    private final String name;

    AbstractSendToToolHandler(String n) {
        name = n;
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract void sendToTool(HttpRequest httpRequest, String name);
}
