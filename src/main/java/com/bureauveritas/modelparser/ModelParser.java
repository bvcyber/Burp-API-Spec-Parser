package com.bureauveritas.modelparser;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.bureauveritas.modelparser.control.BotocorePythonRPC;
import com.bureauveritas.modelparser.model.view.VersionNumber;
import com.bureauveritas.modelparser.view.EDTEventQueueLogger;
import com.bureauveritas.modelparser.view.MainTab;

import javax.swing.*;
import java.awt.*;

public class ModelParser implements BurpExtension {

    private final String extensionName = "API Spec Parser";

    @Override
    public void initialize(MontoyaApi api) {
        Toolkit.getDefaultToolkit().getSystemEventQueue().push(new EDTEventQueueLogger());
        BurpApi.setApi(api);

        api.extension().setName(extensionName);
        BotocorePythonRPC.getInstance();

        SwingUtilities.invokeLater(() -> {
            try {
                api.logging().logToOutput("ModelParser: running UI init on EDT");
                MainTab uiTab = MainTab.getInstance();
                Component rootComponent = uiTab.$$$getRootComponent$$$();
                api.userInterface().applyThemeToComponent(rootComponent);
                api.userInterface().registerSuiteTab(extensionName, rootComponent);
            }
            catch (Exception e) {
                api.logging().logToError(e);
            }
        });

        api.logging().logToOutput(String.format("%s %s", extensionName, VersionNumber.get()));
        api.logging().logToOutput(String.format("Runtime.version(): %s", Runtime.version()));
    }
}
