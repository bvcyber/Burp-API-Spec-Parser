package com.bureauveritas.modelparser.control.file.loader;

import com.bureauveritas.modelparser.control.file.handler.aws.AWSServiceModelFileHandler;
import software.amazon.awssdk.codegen.model.service.ServiceModel;
import software.amazon.awssdk.codegen.utils.ModelLoaderUtils;
import java.io.File;

public class AWSServiceModelFileLoader extends AbstractModelFileLoaderChain<ServiceModel,AWSServiceModelFileHandler> {
    public AWSServiceModelFileLoader() {
        super(AWSServiceModelFileHandler::new);
    }

    @Override
    public ServiceModel loadModel(File file) throws Exception {
        // failOnUnknownProperties is too strict sometimes failing on valid C2Js, so pass false
        // and handle invalid jsons manually
        model = ModelLoaderUtils.loadModel(ServiceModel.class, file, false);
        if (model == null || model.getOperations().isEmpty() || model.getShapes().isEmpty()) {
            throw new Exception("Invalid model");
        }
        return model;
    }

}
