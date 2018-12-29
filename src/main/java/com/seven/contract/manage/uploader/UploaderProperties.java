package com.seven.contract.manage.uploader;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "xin.uploader")
public class UploaderProperties {
    private String commonFilePath;
    private String imagePath;
    private String attachmentPath;
    private String txtFilePath;
    private String childPathStrategy = "month";
    private String domain;
    private String staticFilePath;

    public String getCommonFilePath() {
        return commonFilePath;
    }

    public void setCommonFilePath(String commonFilePath) {
        this.commonFilePath = commonFilePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getChildPathStrategy() {
        return childPathStrategy;
    }

    public void setChildPathStrategy(String childPathStrategy) {
        this.childPathStrategy = childPathStrategy;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getStaticFilePath() {
        return staticFilePath;
    }

    public void setStaticFilePath(String staticFilePath) {
        this.staticFilePath = staticFilePath;
    }

    public String getTxtFilePath() {
        return txtFilePath;
    }

    public void setTxtFilePath(String txtFilePath) {
        this.txtFilePath = txtFilePath;
    }
}
