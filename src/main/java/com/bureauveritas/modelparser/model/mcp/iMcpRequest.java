package com.bureauveritas.modelparser.model.mcp;

public interface iMcpRequest {
    String method();
    String jsonrpc();
    Object id();
}
