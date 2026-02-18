package com.bureauveritas.modelparser.model.view;

import com.bureauveritas.modelparser.BurpApi;

import java.util.Properties;

public class VersionNumber {
    static Properties props = new Properties();

    public static String get() {
        try {
            props.load(VersionNumber.class.getResourceAsStream("/version.properties"));
            return props.getProperty("version");
        } catch (Exception e) {
            BurpApi.getInstance().logging().logToOutput("Failed getting version number");
            BurpApi.getInstance().logging().logToError(e);
        }
        return "error";
    }
}
