package com.bureauveritas.modelparser;

import burp.api.montoya.MontoyaApi;

public class BurpApi {
    private static MontoyaApi api;

    public static void setApi(MontoyaApi a) {
        if (api == null) {
            api = a;
        }
        else {
            throw new RuntimeException("api already set");
        }
    }
    public static MontoyaApi getInstance() {
        return api;
    }
}
