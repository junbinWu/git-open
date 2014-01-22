package com.koudaiv.domain;

import com.koudaiv.constant.AppConstant;

/**
 * 一个文件中每一个模块对应的下载记录
 */
public class ModelDownloadInfo {

    private int threadId;
    private long startPos;
    private long endPos;
    private long completedPos;
    private String url;

    public ModelDownloadInfo(int threadId,long startPos, long endPos ,long completedPos,String url) {
        this.threadId = threadId;
        this.startPos = startPos;
        this.endPos = endPos;
        this.completedPos = completedPos;
        this.url = url;
    }

    public int getThreadId() {
        return threadId;
    }

    public long getStartPos() {
        return startPos;
    }

    public long getEndPos() {
        return endPos;
    }

    public long getCompletedPos() {
        return completedPos;
    }

    public String getUrl() {
        return url;
    }

    public void setCompletedPos(long completedPos) {
        this.completedPos = completedPos;
    }
}
