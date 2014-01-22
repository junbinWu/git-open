package com.koudaiv.service;

import android.content.Context;
import android.os.Message;
import android.widget.ProgressBar;
import com.koudaiv.constant.AppConstant;
import com.koudaiv.dao.DownloadInfoDao;
import com.koudaiv.dao.ModelDownloadInfoDao;
import com.koudaiv.domain.DownloadInfo;
import com.koudaiv.domain.ModelDownloadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import android.os.Handler;
public class DownLoader {

    public static final int DOWNLOAD_THREAD_COUNT = 4;
    public static final int MAX_THREAD_COUNT = 12;
    private Context mContext;
    private String mUrl;
    private ProgressBar mProgressBar;

    public void setOnDownloadProgressChangedListener(OnDownloadProgressChangedListener mListener) {
        this.mListener = mListener;
    }

    private OnDownloadProgressChangedListener mListener;

    private DownloadInfo mDownloadInfo;
    private List<ModelDownloadInfo> mModelDownloadInfoList = new ArrayList<ModelDownloadInfo>();

    public static final int INIT = 0;
    public static final int DOWNLOADING = 1;
    public static final int PAUSE = 2;

    private int downloadState = INIT;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            float f1 = mDownloadInfo.getCompletedPos();
            float f2 = mDownloadInfo.getFileSize();
            float f3 = f1/f2;
            int progress = (int) (f3 * 1000);
            mListener.onProgressChange(mProgressBar, progress);
            if(progress == 1000) {
                mListener.onFinish(mProgressBar);
            }
        }
    };

    private static ThreadPoolExecutor mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_THREAD_COUNT);

    static {
        mThreadPoolExecutor.setThreadFactory(new ThreadFactory(){
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setPriority(Thread.NORM_PRIORITY);
                return thread;
            }
        });
    }

    public DownLoader(Context context, String url , ProgressBar bar) {
        this.mContext = context;
        this.mUrl = url;
        this.mProgressBar = bar;
    }

    public interface OnDownloadProgressChangedListener {
        public void onProgressChange(ProgressBar bar, int progress);
        public void onFinish(ProgressBar bar);
    }

    public boolean isFirst() {
        List<ModelDownloadInfo> list = ModelDownloadInfoDao.getInstance(mContext).getInfos(mUrl);
        if(list != null && list.size() != 0) {
            return false;
        } else {
            return true;
        }
    }

    private void initDownloadInfo() {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(AppConstant.URL + mUrl).openConnection();
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            int length = conn.getContentLength();
            if(length != -1) {
                mDownloadInfo = new DownloadInfo(mUrl,length,0,AppConstant.SD_PATH + mUrl);
                DownloadInfoDao.getInstance(mContext).saveInfos(mDownloadInfo);
                File file = new File(mDownloadInfo.getLocalPath());
                if (!file.exists()) {
                    file.createNewFile();
                }
                long range = length/4;
                mModelDownloadInfoList.clear();
                for(int i=0 ; i < 3 ; i++) {
                    ModelDownloadInfo info = new ModelDownloadInfo(i,i*range,(i+1)*range-1,0,mUrl);
                    mModelDownloadInfoList.add(info);
                }
                ModelDownloadInfo info = new ModelDownloadInfo(3,3*range,length,0,mUrl);
                mModelDownloadInfoList.add(info);
                ModelDownloadInfoDao.getInstance(mContext).saveInfos(mModelDownloadInfoList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(conn!= null) {
                conn.disconnect();
            }
        }
    }

    private void resume() {
        downloadState = DOWNLOADING;
        mDownloadInfo = DownloadInfoDao.getInstance(mContext).getInfos(mUrl);
        mModelDownloadInfoList.clear();
        mModelDownloadInfoList.addAll(ModelDownloadInfoDao.getInstance(mContext).getInfos(mUrl));
    }

    public void download() {
        downloadState = DOWNLOADING;
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(isFirst()) {
                    initDownloadInfo();
                } else {
                    resume();
                }
                for(int i=0 ; i < mModelDownloadInfoList.size() ; i++) {
                    ModelDownloadInfo info = mModelDownloadInfoList.get(i);
                    mThreadPoolExecutor.execute(new CustomRunnable(info));
                }

            }
        });
    }

    public void pause() {
        downloadState = PAUSE;
    }

    public void delete() {

    }

    public boolean isDownloading() {
        return downloadState == DOWNLOADING;
    }

    public long getCompletedPos() {
        return mDownloadInfo.getCompletedPos();
    }

    public long getFileSize() {
        return mDownloadInfo.getFileSize();
    }

    class CustomRunnable implements Runnable {

        private int threadId;
        private long startPos;
        private long endPos;
        private long completedPos;
        private String urlStr;

        private ModelDownloadInfo info;

        public CustomRunnable(ModelDownloadInfo info) {
            this.info = info;
            this.threadId = info.getThreadId();
            this.startPos = info.getStartPos();
            this.endPos = info.getEndPos();
            this.completedPos = info.getCompletedPos();
            this.urlStr = info.getUrl();
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            RandomAccessFile randomAccessFile = null;
            InputStream is = null;
            try {
                URL url = new URL(AppConstant.URL + urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                // 设置范围，格式为Range：bytes x-y;
                connection.setRequestProperty("Range", "bytes="+(startPos + completedPos) + "-" + endPos);
                randomAccessFile = new RandomAccessFile(mDownloadInfo.getLocalPath(), "rwd");
                randomAccessFile.seek(startPos + completedPos);
                // 将要下载的文件写到保存在保存路径下的文件中
                is = connection.getInputStream();
                byte[] buffer = new byte[4096];
                int length = -1;
                while ((length = is.read(buffer)) != -1) {
                    randomAccessFile.write(buffer, 0, length);
                    completedPos += length;
                    mDownloadInfo.setCompletedPos(mDownloadInfo.getCompletedPos() + length);
                    info.setCompletedPos(completedPos);
                    DownloadInfoDao.getInstance(mContext).updateInfos(mDownloadInfo);
                    ModelDownloadInfoDao.getInstance(mContext).updateInfos(info);
                    if(mListener != null) {
                        mHandler.sendEmptyMessage(1);
                    }
                    if (downloadState == PAUSE) {
                        break;
                    }
                    if(mDownloadInfo.getCompletedPos() == mDownloadInfo.getFileSize()) {
                        DownloadInfoDao.getInstance(mContext).deleteInfo(mDownloadInfo.getUrl());
                        ModelDownloadInfoDao.getInstance(mContext).deleteInfos(mDownloadInfo.getUrl());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
