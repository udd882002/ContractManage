package com.seven.contract.manage.uploader;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class EnableUploaderImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] strings = {"com.seven.contract.manage.uploader.UploaderConfiguration"};
        return strings;
    }
}
