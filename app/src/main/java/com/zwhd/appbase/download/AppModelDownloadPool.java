package com.zwhd.appbase.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 下载管理池
 * <p/>
 * Created by YI on 2015/12/7.
 */
public class AppModelDownloadPool {
    private static AppModelDownloadPool ourInstance = new AppModelDownloadPool();

    public static AppModelDownloadPool getInstance() {
        return ourInstance;
    }

    private AppModelDownloadPool() {


    }

    static final int DOWNLOAD_LIMIT = 10;
    /**
     * 当前下载的线程数
     */
    public static Map<String, AppModelDownloadRequest> requests = new ConcurrentHashMap<String, AppModelDownloadRequest>();
    /**
     * 等待下载的数据
     */
    public static List<AppModel> waits = new ArrayList<>();

    /**
     * 将等待改为下载
     */
    public final void moveToDownload(ContentResolver contentResolver) throws Exception {
        if (waits.isEmpty()) return;
        AppModel model = waits.remove(0);
        addDownload(contentResolver, model);
    }

    /**
     * 加入一个下载
     */
    public final void addDownload(ContentResolver contentResolver, AppModel model) throws Exception {
        Set<Map.Entry<String, AppModelDownloadRequest>> entries = requests.entrySet();
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            if (!entry.getValue().isAlive()) requests.remove(entry.getKey());
        }
        if (requests.containsKey(model.getSavePath())) return;
        if (requests.size() >= DOWNLOAD_LIMIT) {
            model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_WAIT);
            model.setActive(1);
            contentResolver.update(Uri.parse(AppModelProvider.APPS_URI), AppModelUtil.mdlToCvs(model), " _id=?", new String[]{String.valueOf(model.getId())});
            waits.add(model);
            AppConstant.app.sendBroadcast(new Intent(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION));
        } else {
            switch (model.getDownloadStatus()) {
                case AppModel.DownloadStatus.DOWNLOAD_ERR:
                case AppModel.DownloadStatus.DOWNLOAD_ING:
                case AppModel.DownloadStatus.DOWNLOAD_NOT:
                case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                    AppModelDownloadRequest request = new AppModelDownloadRequest(model, contentResolver);
                    request.start();
                    requests.put(model.getSavePath(), request);
                    break;
            }
        }
    }

    /**
     * 全部停止
     */
    public static final void pauseAll() {
        Set<Map.Entry<String, AppModelDownloadRequest>> entries = requests.entrySet();
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            if (entry.getValue().isAlive()) entry.getValue().setIsDownload(false);
        }
        waits.clear();
        requests.clear();
    }

    /**
     * 暂停某一个
     */
    public static final void pauseById(ContentResolver contentResolver, long id) {
        Set<Map.Entry<String, AppModelDownloadRequest>> entries = requests.entrySet();
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            AppModelDownloadRequest request = entry.getValue();
            if (request.model.getId() == id) {
                request.setIsDownload(false);
                requests.remove(request.model.getSavePath());
            }
        }
        removeWait(contentResolver, id);
        try {
            AppModelDownloadPool.getInstance().moveToDownload(contentResolver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        AppConstant.app.sendBroadcast(new Intent(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION));
    }

    static Object obj = new Object();

    /**
     * 移除某个等待
     */
    public static final void removeWait(ContentResolver contentResolver, long id) {
        synchronized (obj) {
            int index = -1;
            for (int i = 0, leg = waits.size(); i < leg; i++) {
                if (waits.get(i).getId() == id) {
                    index = i;
                }
            }
            if (index != -1) {
                AppModel model = waits.remove(index);
                model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_PAUSE);
                ContentValues values = AppModelUtil.mdlToCvs(model);
                contentResolver.update(Uri.parse(AppModelProvider.APPS_URI), values, " _id=? ", new String[]{String.valueOf(model.getId())});
                AppConstant.app.sendBroadcast(new Intent(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION));
            }
        }
    }


    /**
     * 当前的下载线程数
     */
    public static final int getDownloadCount() {
        Set<Map.Entry<String, AppModelDownloadRequest>> entries = requests.entrySet();
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            if (!entry.getValue().isAlive()) requests.remove(entry.getKey());
        }
        return requests.size();
    }

    /**
     * 更新全部的信息
     */
    public static final void updateAllStatus(ContentResolver contentResolver) {
        int count = getDownloadCount();
        if (count <= 0) {
            ContentValues values = new ContentValues();
            values.put("_downloadStatus", AppModel.DownloadStatus.DOWNLOAD_PAUSE);
            contentResolver.update(Uri.parse(AppModelProvider.APPS_URI), values, "_active=1 and (_downloadStatus=? or _downloadStatus=? or  _downloadStatus=?)",
                    new String[]{String.valueOf(AppModel.DownloadStatus.DOWNLOAD_ING),
                            String.valueOf(AppModel.DownloadStatus.DOWNLOAD_PAUSE),
                            String.valueOf(AppModel.DownloadStatus.DOWNLOAD_WAIT)});
        }
    }

    /**
     * 附加视图到下载请求对象
     */
    public static final void attachViewToDownload(AppModel model,TextView status, TextView progress, ProgressBar... pbs) {
        if (model == null) return;
        Set<Map.Entry<String, AppModelDownloadRequest>> entries = requests.entrySet();
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            if (!entry.getValue().isAlive()) requests.remove(entry.getKey());
        }
        for (Map.Entry<String, AppModelDownloadRequest> entry : entries) {
            AppModelDownloadRequest request = entry.getValue();
            if (request.model.getId() == model.getId()) {
                request.progress.clear();
                if (progress != null) request.progress.add(progress);
                request.status.clear();
                if(status!=null)request.status.add(status);
                request.progressBars.clear();
                if(pbs!=null&&pbs.length>0){
                    for(ProgressBar pb:pbs){
                        request.progressBars.add(pb);
                    }
                }
            }
        }
    }
}
