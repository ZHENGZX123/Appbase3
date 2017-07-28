package com.zwhd.appbase.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.INotificationSideChannel;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.Logger;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * 数据模型下载
 * Created by YI on 2015/12/10.
 */
public class AppModelDownloadRequest extends Thread {
    /**
     * 数据模型
     */
    AppModel model;
    /**
     * 更新数据的地址
     */
    Uri uri = Uri.parse(AppModelProvider.APPS_URI);
    /**
     * 是否延续下载
     */
    boolean isDownload = true;
    /**
     * 当前下载的比例
     */
    long size;
    ContentResolver contentResolver;
    final static Handler HANDLER = new Handler();
    /**
     * 待更新的进度条列表
     */
    public Set<ProgressBar> progressBars = new HashSet<>();
    /**
     * 待更新的进度
     */
    public Set<TextView> progress = new HashSet<>();
    /**
     * 待更新的状态展示
     */
    public Set<TextView> status = new HashSet<>();


    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }

    public AppModelDownloadRequest(AppModel model, ContentResolver contentResolver) {
        this.model = model;
        this.contentResolver = contentResolver;
        Logger.log(model);
    }

    @Override
    public void run() {
        super.run();
        BufferedInputStream bis = null;
        HttpURLConnection con = null;
        try {
            File f = new File(model.getSavePath());
            if (!f.exists()) model.setDownloadSize(0);
            else model.setDownloadSize(f.length());
            RandomAccessFile file = new RandomAccessFile(f.getAbsolutePath(), "rw");
            file.seek(model.getDownloadSize());
            URL url = new URL(model.getaDwonloadUrl());
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(15 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setRequestProperty("RANGE", "bytes=" + model.getDownloadSize() + "-");
            con.setRequestProperty("Connection", "Keep-Alive");
            con.setRequestProperty("Charset", "UTF-8");
            if (con != null && (con.getResponseCode() == HttpURLConnection.HTTP_OK || con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL)) {
                if (model.getAppSize() <= 0) model.setAppSize(con.getContentLength());
                model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_ING);
                updateData();
                bis = new BufferedInputStream(con.getInputStream());
                byte[] b = new byte[1024];
                int length;
                while (((length = bis.read(b)) != -1) && isDownload) {
                    model.setDownloadSize(model.getDownloadSize() + length);
                    file.write(b, 0, length);
                    size += length;
                    if ((model.getAppSize() / size) < 100) {//每10%更新一次
                        size = 0;
                        updateData(false);
                    }
                }
                if ((model.getAppSize() > 0) && (model.getDownloadSize() >= model.getAppSize())) {//下载完成
                    model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_COMPLETE);
                    //model.setPackageName(AppUtil.loadFilePkgName(AppConstant.app,new File(model.getSavePath())));
                    updateData();
                    AppUtil.installApp(AppConstant.app, new File(model.getSavePath()));
                    try {
                        Intent intent = new Intent(AppConstant.APP_DOWNLOAD_SUCCESS_ACTION);
                        intent.putExtra(IConstant.BUNDLE_PARAMS, model);
                        AppConstant.app.sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!isDownload) {
                    model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_PAUSE);
                    updateData();
                }
            } else {
                model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_ERR);
                updateData();
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_ERR);
            updateData();
        } finally {
            try {
                if (bis != null)
                    bis.close();
                if (con != null)
                    con.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        AppModelDownloadPool.requests.remove(model.getSavePath());
        try {
            AppModelDownloadPool.getInstance().moveToDownload(contentResolver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新下载状态
     */
    void updateData(final boolean... bl) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                model.setActive(1);
                int pg = (int) ((float) model.getDownloadSize() / (float) model.getAppSize() * 100);
                if (pg > 100) pg = 0;
                for (ProgressBar progressBar : progressBars) {
                    if (progressBar != null) {
                        progressBar.setProgress(pg);
                    }
                }
                for (TextView tv : progress) {
                    if (tv != null) tv.setText(pg + "%");
                }
                for (TextView tv : status) {
                    if (tv != null) tv.setText(AppModelUtil.getDownloadStatusStr(model));
                }
                ContentValues values = AppModelUtil.mdlToCvs(model);
                contentResolver.update(uri, values, " _id=? ", new String[]{String.valueOf(model.getId())});
                if (bl == null || bl.length == 0)
                    AppConstant.app.sendBroadcast(new Intent(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION));
            }
        });
    }
}
