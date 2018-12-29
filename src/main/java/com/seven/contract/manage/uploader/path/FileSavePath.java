package com.seven.contract.manage.uploader.path;

public interface FileSavePath {
    String fullPath(int type, String addPath);

    String relativePath(int type, String addPath);

    String relativePath2fullPath(String relativePath);
}
