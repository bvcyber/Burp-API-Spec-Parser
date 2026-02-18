package com.bureauveritas.modelparser.control.file.loader;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.bureauveritas.modelparser.BurpApi;
import com.bureauveritas.modelparser.control.file.handler.AbstractModelFileHandler;
import io.swagger.v3.core.util.Json;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.yaml.snakeyaml.LoaderOptions;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractModelFileLoaderChain<T,TT extends AbstractModelFileHandler<?>> implements iModelFileLoader {
    protected iModelFileLoader next;
    protected T model;
    protected final Function<T, TT> handlerFactory;
    protected Map<String,Object> additionalProperties = new HashMap<>();

    public AbstractModelFileLoaderChain(Function<T,TT> handlerFactory) {
        this.handlerFactory = handlerFactory;
    }

    @Override
    public AbstractModelFileHandler<?> getModelFileHandler(File file) {
        try {
            BurpApi.getInstance().logging().logToOutput(
                String.format("Attempting %s...", this.getClass().getSimpleName()));
            additionalProperties.clear();
            TT modelFileHandler = handlerFactory.apply(loadModel(file));
            modelFileHandler.setFileContent(getModelFileContent(file));
            modelFileHandler.setAdditionalProperties(additionalProperties);
            return modelFileHandler;
        }
        catch (Exception e) {
            BurpApi.getInstance().logging().logToError(
                String.format("Skipping %s (reason: %s)...", this.getClass().getSimpleName(), e.getMessage()));
            BurpApi.getInstance().logging().logToError(e);
            if (next != null) {
                return next.getModelFileHandler(file);
            }
            return null;
        }
    }

    public AbstractModelFileLoaderChain<?,?> setNext(AbstractModelFileLoaderChain<?,?> next) {
        this.next = next;
        return next;
    }

    private String getModelFileContent(File file) throws IOException {
        String openFileContentAsJson = FileUtils.readFileToString(file, "UTF-8");
        String fileExtension = FilenameUtils.getExtension(file.getName());
        if (fileExtension.equalsIgnoreCase("yaml") ||
            fileExtension.equalsIgnoreCase("yml")) {
            // Convert yaml to json since we rely on JMESPath for parsing
            // TODO: indicate in UI that YAML was converted to JSON
            LoaderOptions yamlLoaderOptions = new LoaderOptions();
            yamlLoaderOptions.setCodePointLimit(1024 * 1024 * 1024);
            ObjectMapper yamlMapper = new ObjectMapper(YAMLFactory.builder().loaderOptions(yamlLoaderOptions).build());
            JsonNode jsonNode = yamlMapper.readTree(openFileContentAsJson);
            openFileContentAsJson = Json.mapper().writeValueAsString(jsonNode);
        }
        return openFileContentAsJson;
    }

    abstract public T loadModel(File file) throws Exception;
}
