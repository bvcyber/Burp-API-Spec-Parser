package com.bureauveritas.modelparser.model;

import lombok.Getter;
import lombok.Setter;

public class Settings {
    @Getter @Setter
    private static boolean invalidOpenAPIHostAllowed = false;
    @Getter @Setter
    private static int grpcPortNumber = 50055;
    @Getter @Setter
    private static int mcpConnectionTimeoutSeconds = 5;
}
