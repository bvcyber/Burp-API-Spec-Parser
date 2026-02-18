package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.control.file.handler.AbstractModelFileHandler;

import java.io.File;

public interface iModelFileLoader {
    AbstractModelFileHandler<?> getModelFileHandler(File file);
}
