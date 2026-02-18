package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.control.file.handler.mcp.McpServersJsonFileHandler;
import com.bureauveritas.modelparser.model.mcp.McpServersJsonFileModel;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;

public class McpServersJsonFileLoader extends AbstractModelFileLoaderChain<McpServersJsonFileModel, McpServersJsonFileHandler> {
    private static final ObjectMapper jsonMapper = JsonMapper.builder()
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

    public McpServersJsonFileLoader() {
        super(McpServersJsonFileHandler::new);
    }

    @Override
    public McpServersJsonFileModel loadModel(File file) {
        model = jsonMapper.readValue(file, McpServersJsonFileModel.class);
        return model;
    }
}
