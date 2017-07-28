package com.zwhd.appbase;

import com.zwhd.appbase.http.BaseHttpConnectPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 常用数据定义
 * Created by YI on 2015/11/28.
 */
public interface IConstant {
    /**
     * 请求连接池
     */
    public BaseHttpConnectPool HTTP_CONNECT_POOL = BaseHttpConnectPool.getInstance();
    /**
     * 线程管理
     */
    public ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    /**
     * 参数传递
     */
    public String BUNDLE_PARAMS = "bundle_params";
    public String BUNDLE_PARAMS1 = "bundle_params1";
    /**
     * 图像的上传尺寸
     */
    public int UPLOAD_PHOTO_SIZE = 500;
    /**
     * 调用拍照返回码
     */
    public int FOR_CAMERA = 1001;
    /**
     * 调用相册返回码
     */
    public int FOR_PHOTO = 1002;
    /**
     * 调用截图返回码
     */
    public int FOR_CROP = 1003;
    /**
     * 一般的数据返回
     */
    public int FOR_BUNDLE = 1004;
    /**
     * 请求页面大小
     * */
    public int REQUEST_PAGE_SIZE=50;





    /*---------------------------------------文件相关------------------------------------------------*/
    /**
     * 缓存文件
     */
    public String ZWHD_ROOT = "zwhd_indiana";
    /**
     * 录制的音频文件目录
     */
    public final String RECORDER_AUDIO_FLODER = "recorder_audios";
    /**
     * 下载
     */
    public final String DOWNLOAD_FILES = "download_files";
    /**
     * 下载的音频文件目录
     */
    public final String DOWNLOAD_AUDIO_FLODER = "download_audios";
    /**
     * 拍摄的照片目录
     */
    public final String CAMERA_PHOTO_FLODER = "carera_photos";
    /**
     * 下载的照片目录
     */
    public final String DOWNLOAD_PHOTO_FLODER = "download_photos";
    /**
     * 录制的视频目录
     */
    public final String RECORDER_VIDEO_FLODER = "recorder_videos";
    /**
     * 下载的视频目录
     */
    public final String DOWNLOAD_VIDEO_FLODER = "download_videos";
     /*---------------------------------------文件相关------------------------------------------------*/
}
