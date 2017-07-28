package com.zwhd.appbase.download;

import android.os.Environment;

import java.io.File;
import java.io.Serializable;

/**
 * APP数据模型
 * <p/>
 * Created by YI on 2015/12/9.
 */
public class AppModel implements Serializable {
    /**
     * 文件保存的地址
     */
    static final String BASE_PATH = "appbase";
    static final String SAVE_PATH = "downloads";

    /**
     * 数据库id
     */
    long id;
    /**
     * 后台id
     */
    long aId;
    /**
     * 分类id
     */
    int aCateId;
    /**
     * 版本号
     */
    int aVersion;
    /**
     * 版本名称
     */
    String aVersionName;
    /**
     * 更新时间
     */
    String aUpdateTime;
    /**
     * 软件大小
     */
    String aSize;
    /**
     * 兼容系统
     */
    String aContainSystem;
    /**
     * 图标地址
     */
    String aLogo;
    /**
     * 年龄分级
     */
    String aGrade;
    /**
     * 软件评分
     */
    String aRecomment;
    /**
     * 软件发布商
     */
    String aName;
    /**
     * 软件评论数
     */
    String aComments;
    /**
     * 搜索次数
     */
    String aVisitors;
    /**
     * 下载次数
     */
    String aDown;
    /**
     * 应用简介
     */
    String aDesc;
    /**
     * 下载地址
     */
    String aDwonloadUrl;
    /**
     * 应用包名
     */
    String packageName;
    /**
     * 文件保存地址
     */
    String savePath;
    /**
     * 激活状态   激活为1   默认为0
     */
    int active;
    /**
     * 已下载的数据大小
     */
    long downloadSize;
    /**
     * 应用的具体数据大小
     */
    long appSize;
    /**
     * 下载状态
     */
    int downloadStatus;
    /**
     * 打开方式
     */
    int openway;
    /**
     * 软件名称
     */
    String aTitle;
    /**
     * 广告地址
     */
    String advurl;

    public String getAdvurl() {
        return advurl;
    }

    public void setAdvurl(String advurl) {
        this.advurl = advurl;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getaId() {
        return aId;
    }

    public void setaId(long aId) {
        this.aId = aId;
    }

    public int getaCateId() {
        return aCateId;
    }

    public void setaCateId(int aCateId) {
        this.aCateId = aCateId;
    }

    public int getaVersion() {
        return aVersion;
    }

    public void setaVersion(int aVersion) {
        this.aVersion = aVersion;
    }

    public String getaVersionName() {
        return aVersionName;
    }

    public void setaVersionName(String aVersionName) {
        this.aVersionName = aVersionName;
    }

    public String getaUpdateTime() {
        return aUpdateTime;
    }

    public void setaUpdateTime(String aUpdateTime) {
        this.aUpdateTime = aUpdateTime;
    }

    public String getaSize() {
        return aSize;
    }

    public void setaSize(String aSize) {
        this.aSize = aSize;
    }

    public String getaContainSystem() {
        return aContainSystem;
    }

    public void setaContainSystem(String aContainSystem) {
        this.aContainSystem = aContainSystem;
    }

    public String getaLogo() {
        return aLogo;
    }

    public void setaLogo(String aLogo) {
        this.aLogo = aLogo;
    }

    public String getaGrade() {
        return aGrade;
    }

    public void setaGrade(String aGrade) {
        this.aGrade = aGrade;
    }

    public String getaRecomment() {
        return aRecomment;
    }

    public void setaRecomment(String aRecomment) {
        this.aRecomment = aRecomment;
    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaComments() {
        return aComments;
    }

    public void setaComments(String aComments) {
        this.aComments = aComments;
    }

    public String getaVisitors() {
        return aVisitors;
    }

    public void setaVisitors(String aVisitors) {
        this.aVisitors = aVisitors;
    }

    public String getaDown() {
        return aDown;
    }

    public void setaDown(String aDown) {
        this.aDown = aDown;
    }

    public String getaDesc() {
        return aDesc;
    }

    public void setaDesc(String aDesc) {
        this.aDesc = aDesc;
    }

    public String getaDwonloadUrl() {
        return aDwonloadUrl;
    }

    public void setaDwonloadUrl(String aDwonloadUrl) {
        this.aDwonloadUrl = aDwonloadUrl;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public long getDownloadSize() {
        return downloadSize;
    }

    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getOpenway() {
        return openway;
    }

    public void setOpenway(int openway) {
        this.openway = openway;
    }

    public String getaTitle() {
        return aTitle;
    }

    public void setaTitle(String aTitle) {
        this.aTitle = aTitle;
    }

    /**
     * 获取数据保存的地址
     */
    public String getGeneralPath() {
        File f = new File(Environment.getExternalStorageDirectory(), BASE_PATH);
        if (!f.exists()) f.mkdirs();
        f = new File(f, SAVE_PATH);
        if (!f.exists()) f.mkdirs();
        return new File(f, getaTitle() + "_" + getPackageName() + "_" + getaVersionName() + "_" + getaVersion() + ".apk").getAbsolutePath();
    }

    @Override
    public String toString() {
        return "AppModel{" +
                "id=" + id +
                ", aId=" + aId +
                ", aCateId=" + aCateId +
                ", aVersion=" + aVersion +
                ", aVersionName='" + aVersionName + '\'' +
                ", aUpdateTime='" + aUpdateTime + '\'' +
                ", aSize='" + aSize + '\'' +
                ", aContainSystem='" + aContainSystem + '\'' +
                ", aLogo='" + aLogo + '\'' +
                ", aGrade='" + aGrade + '\'' +
                ", aRecomment='" + aRecomment + '\'' +
                ", aName='" + aName + '\'' +
                ", aComments='" + aComments + '\'' +
                ", aVisitors='" + aVisitors + '\'' +
                ", aDown='" + aDown + '\'' +
                ", aDesc='" + aDesc + '\'' +
                ", aDwonloadUrl='" + aDwonloadUrl + '\'' +
                ", packageName='" + packageName + '\'' +
                ", savePath='" + savePath + '\'' +
                ", active=" + active +
                ", downloadSize=" + downloadSize +
                ", appSize=" + appSize +
                ", downloadStatus=" + downloadStatus +
                ", openway=" + openway +
                ", aTitle='" + aTitle + '\'' +
                '}';
    }

    /**
     * 下载状态
     */
    public interface DownloadStatus {

        /**
         * 下载完成
         */
        public static final int DOWNLOAD_COMPLETE = 0;
        /**
         * 下载暂停
         */
        public static final int DOWNLOAD_PAUSE = 1;
        /**
         * 正在下载
         */
        public static final int DOWNLOAD_ING = 2;
        /**
         * 下载错误
         */
        public static final int DOWNLOAD_ERR = 3;
        /**
         * 等待下载
         */
        public static final int DOWNLOAD_WAIT = 4;
        /**
         * 未下载
         */
        public static final int DOWNLOAD_NOT = 5;
        /**
         * 已安装
         */
        public static final int INSTALLED = 6;
        /**
         * 升级
         */
        public static final int UPGRADE = 7;
    }

    /**
     * 打开方式
     */
    public interface OpenWay {
        /**
         * 默认方式下载
         */
        public static final int DEFAULT = 0;
        /**
         * 调用浏览器
         */
        public static final int BROSWER = 1;
    }
}
