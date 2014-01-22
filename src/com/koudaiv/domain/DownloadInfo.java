package com.koudaiv.domain;

/**
 * 一个文件对应的下载信息
 */
public class DownloadInfo {
    private String url;
    private long fileSize;
    private long completedPos;
    private String localPath;

    public DownloadInfo(String url,long fileSize,long completedPos ,String localPath) {
        this.url = url;
        this.fileSize = fileSize;
        this.completedPos = completedPos;
        this.localPath = localPath;
    }

    public String getUrl() {
        return url;
    }

    public long getFileSize() {
        return fileSize;
    }

    public long getCompletedPos() {
        return completedPos;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setCompletedPos(long completedPos) {
        this.completedPos = completedPos;
    }

}
