package com.bureauveritas.modelparser.control;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.bureauveritas.modelparser.BurpApi;
import io.burt.jmespath.Expression;
import io.burt.jmespath.JmesPath;
import io.burt.jmespath.gson.GsonRuntime;

public class JMESPathUtils {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final JmesPath<JsonElement> jmesPath = new GsonRuntime();
    private final JsonElement jsonInput;

    public JMESPathUtils(String content) {
        jsonInput = gson.fromJson(content, JsonElement.class);
    }

    public String executeQuery(String query) {
        return compileAndExecute(query, jsonInput);
    }

    public static String executeQuery(String content, String query) {
        try {
            JsonElement input = gson.fromJson(content, JsonElement.class);
            return compileAndExecute(query, input);
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            return e.getMessage();
        }
    }

    private static String compileAndExecute(String query, JsonElement input) {
        try {
            Expression<JsonElement> expression = jmesPath.compile(query);
            String result = gson.toJson(expression.search(input));
            if (result.equals("null")) {
                return null;
            }
            return result;
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(e);
            return e.getMessage();
        }
    }
}
