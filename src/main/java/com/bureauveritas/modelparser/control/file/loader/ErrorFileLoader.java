package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.control.file.handler.ErrorFileHandler;

import java.io.File;

public class ErrorFileLoader extends AbstractModelFileLoaderChain<Object,ErrorFileHandler> {

    public ErrorFileLoader() {
        super(ErrorFileHandler::new);
    }

    @Override
    public Object loadModel(File file) {
        return null;
    }
}
