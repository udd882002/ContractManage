package com.seven.contract.manage.uploader.path.impl;

import com.seven.contract.manage.uploader.UploaderProperties;
import com.seven.contract.manage.uploader.path.ChildPath;
import com.seven.contract.manage.uploader.path.FileSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FileSavePathImpl implements FileSavePath {

    private Logger logger = LoggerFactory.getLogger(FileSavePathImpl.class);

    @Autowired
    private UploaderProperties uploaderProperties;

    @Autowired
    private ChildPath childPath;

    @Override
    public String fullPath(int type, String addPath) {
        if (addPath == null || addPath.equals("")) {
            return rootPath(type) + childPath();
        } else {
            return rootPath(type) + "/" + addPath + childPath();
        }
    }

    @Override
    public String relativePath(int type , String addPath) {
        switch (type) {
            case 1:
                if (addPath == null || addPath.equals("")) {
                    return uploaderProperties.getDomain() + "/fileupload/file" + childPath();
                } else {
                    return uploaderProperties.getDomain() + "/fileupload/file/" + addPath + childPath();
                }
            case 2:
                if (addPath == null || addPath.equals("")) {
                    return uploaderProperties.getDomain() + "/fileupload/image" + childPath();
                } else {
                    return uploaderProperties.getDomain() + "/fileupload/image/" + addPath + childPath();
                }
            case 3:
                if (addPath == null || addPath.equals("")) {
                    return uploaderProperties.getDomain() + "/fileupload/attachment" + childPath();
                } else {
                    return uploaderProperties.getDomain() + "/fileupload/attachment/" + addPath + childPath();
                }
            case 4:
                if (addPath == null || addPath.equals("")) {
                    return uploaderProperties.getDomain() + "/fileupload/txtfile" + childPath();
                } else {
                    return uploaderProperties.getDomain() + "/fileupload/txtfile/" + addPath + childPath();
                }
            default:
                return "";
        }
    }

    @Override
    public String relativePath2fullPath(String relativePath) {

        logger.debug("relativePath = {}", relativePath);

        String fullPath = uploaderProperties.getStaticFilePath() + relativePath.substring(relativePath.indexOf("/fileupload/"));

        logger.debug("fullPath = {}", fullPath);

        return fullPath;
    }

    private String childPath() {
        return childPath.getChildPath();
    }

    private String rootPath(int type) {
        switch (type) {
            case 1:
                return uploaderProperties.getCommonFilePath();
            case 2:
                return uploaderProperties.getImagePath();
            case 3:
                return uploaderProperties.getAttachmentPath();
            case 4:
                return uploaderProperties.getTxtFilePath();
            default:
                return "";
        }
    }

    public static void main(String[] args) {
        String url = "http://39.96.22.77:80/fileupload/attachment/18888888888/2018/12/24/滴滴出行行程报销单152702_2.jpg";

        System.out.println("/data/works" + url.substring(url.indexOf("/fileupload/")));
    }

}
