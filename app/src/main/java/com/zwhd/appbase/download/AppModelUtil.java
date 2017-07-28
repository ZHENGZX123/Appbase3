package com.zwhd.appbase.download;

import android.app.Application;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.tech.NfcA;
import android.os.Environment;
import android.os.Handler;


import com.zwhd.appbase.R;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.Logger;
import com.zwhd.appbase.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 应用模型工具集
 * <p/>
 * Created by YI on 2015/12/9.
 */
public final class AppModelUtil {
    static final Uri uri = Uri.parse(AppModelProvider.APPS_URI);
    static Handler handler = new Handler();
    static Object obj = new Object();

    /**
     * 插入数据
     */
    public static final void insert(ContentResolver contentResolver, int cateId, List<AppModel> models) throws Exception {
        synchronized (obj) {
            if (contentResolver == null || models == null || models.size() == 0) return;
            List<AppModel> inserts = new ArrayList<>();
            for (AppModel model : models) {
                List<AppModel> appModels = queryByCateIdandAid(contentResolver, cateId, model.getaId());
                boolean bl = isSaveInDB(contentResolver, model.aCateId, model.getaId());
                boolean bl1 = AppUtil.isInstallPkg(AppConstant.app.getApplicationContext(), model.getPackageName());
                if (!bl) {//表示还没有保存到本地数据库
                    model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_NOT);
                    if (bl1) model.setDownloadStatus(AppModel.DownloadStatus.INSTALLED);
                    inserts.add(model);
                } else {
                    AppModel am = appModels.get(0);
                    if (bl1) am.setDownloadStatus(AppModel.DownloadStatus.INSTALLED);
                    setModelData(am, model);
                    contentResolver.update(uri, mdlToCvs(am), " _id=? ", new String[]{String.valueOf(am.getId())});
                }
            }
            if (inserts.size() > 0) {
                Logger.log(inserts);
                contentResolver.bulkInsert(uri, mdlToCvs(inserts));
            }
        }
    }

    /**
     * 设置新的网络数据
     *
     * @param a 需要返回的数据
     * @param b 源数据
     */
    public static final AppModel setModelData(AppModel a, AppModel b) {
        if (a == null || b == null) return null;
        a.setaVersion(b.getaVersion());
        a.setaVersionName(b.getaVersionName());
        a.setaUpdateTime(b.getaUpdateTime());
        a.setaSize(b.getaSize());
        a.setaContainSystem(b.getaContainSystem());
        a.setaLogo(b.getaLogo());
        a.setaGrade(b.getaGrade());
        a.setaRecomment(b.getaRecomment());
        a.setaName(b.getaName());
        a.setaComments(b.getaComments());
        a.setaVisitors(b.getaVisitors());
        a.setaDown(b.getaDown());
        a.setaDesc(b.getaDesc());
        a.setaDwonloadUrl(b.getaDwonloadUrl());
        if (StringUtil.isNotEmpty(b.getPackageName())) a.setPackageName(b.getPackageName());
        a.setaTitle(b.getaTitle());
        a.setAdvurl(b.getAdvurl());
        return a;
    }


    /**
     * 查找某分组下的数据
     */
    public static final List<AppModel> queryByCateId(ContentResolver contentResolver, int cateId) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _aCateId=? ", new String[]{String.valueOf(cateId)}, null);
        return csrToMdl(cursor, true);
    }


    /**
     * 查找某分组下的数据
     */
    public static final List<AppModel> queryByCateIdandName(ContentResolver contentResolver, int cateId, String name) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _aCateId=? ", new String[]{String.valueOf(cateId)}, null);

        List<AppModel> list = csrToMdl(cursor, true);
        if (list != null) {
            List<AppModel> models = new ArrayList<>();
            for (AppModel model : list) {
                if (model.getaTitle().toLowerCase(Locale.getDefault()).contains(name.toLowerCase(Locale.getDefault()))) {
                    models.add(model);
                }
            }
            return models;
        } else {
            return null;
        }
    }

    /**
     * 查找某分组某id的数据
     */
    public static final List<AppModel> queryByCateIdandAid(ContentResolver contentResolver, int cateId, long aId) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _aCateId=? and _aId=? ", new String[]{String.valueOf(cateId), String.valueOf(aId)}, null);
        return csrToMdl(cursor, true);
    }

    /**
     * 根据分组分页查询
     */
    public static final List<AppModel> queryByPage(ContentResolver contentResolver, int cateId, int start, int size) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _aCateId=? ", new String[]{String.valueOf(cateId)}, " _id desc  limit " + size + " offset " + start);
        return csrToMdl(cursor, true);
    }


    /**
     * 根据分组分页查询
     */
    public static final List<AppModel> queryByPage(ContentResolver contentResolver, int cateId, int start, int size, String name) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _aCateId=? and _aTitle not like null ", new String[]{String.valueOf(cateId)}, " _id desc  limit " + size + " offset " + start);
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            Logger.log(cursor.getString(24));
            cursor.moveToPrevious();
        }
        return csrToMdl(cursor, true);
    }

    /**
     * 查找所有已激活的数据
     */
    public static final List<AppModel> queryInActive(ContentResolver contentResolver) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _active=1 ", null, null);
        return csrToMdl(cursor, true);
    }

    /**
     * 删除下载
     */
    public static final void deleteDownload(ContentResolver contentResolver, AppModel... models) {
        if (contentResolver == null) return;
        if (models == null || models.length == 0) return;
        ContentValues values = new ContentValues();
        values.put("_active", 0);
        values.put("_downloadStatus", AppModel.DownloadStatus.DOWNLOAD_NOT);
        values.put("_downloadSize", 0);
        for (AppModel model : models) {
            AppModelDownloadPool.removeWait(contentResolver, model.getId());
            contentResolver.update(uri, values, " _id=? ", new String[]{String.valueOf(model.getId())});
            File f = new File(model.getSavePath());
            if (f.exists()) f.delete();
        }
    }

    /**
     * 验证该数据对象是否已经存在于数据库
     */
    public static final boolean isSaveInDB(ContentResolver contentResolver, int cateId, long aId) {
        List<AppModel> models = queryByCateIdandAid(contentResolver, cateId, aId);
        if (models == null || models.size() == 0) return false;
        return true;
    }

    /**
     * 游标转对象
     *
     * @param cursor   游标
     * @param closeCsr 遍历完成是否关闭游标（可选）
     */
    public static final List<AppModel> csrToMdl(Cursor cursor, boolean... closeCsr) {
        if (cursor == null || cursor.isClosed()) return null;
        List<AppModel> models = new ArrayList<>();
        while (cursor.moveToNext()) {
            AppModel model = new AppModel();
            model.setId(cursor.getLong(0));
            model.setaId(cursor.getLong(1));
            model.setaCateId(cursor.getInt(2));
            model.setaVersion(cursor.getInt(3));
            model.setaVersionName(cursor.getString(4));
            model.setaUpdateTime(cursor.getString(5));
            model.setaSize(cursor.getString(6));
            model.setaContainSystem(cursor.getString(7));
            model.setaLogo(cursor.getString(8));
            model.setaGrade(cursor.getString(9));
            model.setaRecomment(cursor.getString(10));
            model.setaName(cursor.getString(11));
            model.setaComments(cursor.getString(12));
            model.setaVisitors(cursor.getString(13));
            model.setaDown(cursor.getString(14));
            model.setaDesc(cursor.getString(15));
            model.setaDwonloadUrl(cursor.getString(16));
            model.setPackageName(cursor.getString(17));
            model.setSavePath(cursor.getString(18));
            model.setActive(cursor.getInt(19));
            model.setDownloadSize(cursor.getLong(20));
            model.setAppSize(cursor.getLong(21));
            model.setDownloadStatus(cursor.getInt(22));
            model.setOpenway(cursor.getInt(23));
            model.setaTitle(cursor.getString(24));
            model.setAdvurl(cursor.getString(25));
            models.add(model);
        }
        if (closeCsr != null && closeCsr.length > 0) {
            boolean bl = closeCsr[0];
            if (bl) cursor.close();
        }
        return models;
    }

    /**
     * 对象转内容
     *
     * @param models 对象列表
     */
    public static final ContentValues[] mdlToCvs(List<AppModel> models) {
        int leg = 0;
        if (models == null || (leg = models.size()) == 0) return null;
        ContentValues[] values = new ContentValues[models.size()];
        for (int i = 0; i < leg; i++) {
            AppModel model = models.get(i);
            ContentValues value = new ContentValues();
            //value.put("_id", model.getId());
            value.put("_aId", model.getaId());
            value.put("_aCateId", model.getaCateId());
            value.put("_aVersion", model.getaVersion());
            value.put("_aVersionName", model.getaVersionName());
            value.put("_aUpdateTime", model.getaUpdateTime());
            value.put("_aSize", model.getaSize());
            value.put("_aContainSystem", model.getaContainSystem());
            value.put("_aLogo", model.getaLogo());
            value.put("_aGrade", model.getaGrade());
            value.put("_aRecomment", model.getaRecomment());
            value.put("_aName", model.getaName());
            value.put("_aComments", model.getaComments());
            value.put("_aVisitors", model.getaVisitors());
            value.put("_aDown", model.getaDown());
            value.put("_aDesc", model.getaDesc());
            value.put("_aDwonloadUrl", model.getaDwonloadUrl());
            value.put("_packageName", model.getPackageName());
            value.put("_savePath", model.getSavePath());
            value.put("_active", model.getActive());
            value.put("_downloadSize", model.getDownloadSize());
            value.put("_appSize", model.getAppSize());
            value.put("_downloadStatus", model.getDownloadStatus());
            value.put("_openway", model.getOpenway());
            value.put("_aTitle", model.getaTitle());
            value.put("_advurl", model.getAdvurl());
            values[i] = value;
        }
        return values;
    }

    /**
     * 对象转内容
     *
     * @param model 对象
     */
    public static final ContentValues mdlToCvs(AppModel model) {
        ContentValues value = new ContentValues();
        //value.put("_id", model.getId());
        value.put("_aId", model.getaId());
        value.put("_aCateId", model.getaCateId());
        value.put("_aVersion", model.getaVersion());
        value.put("_aVersionName", model.getaVersionName());
        value.put("_aUpdateTime", model.getaUpdateTime());
        value.put("_aSize", model.getaSize());
        value.put("_aContainSystem", model.getaContainSystem());
        value.put("_aLogo", model.getaLogo());
        value.put("_aGrade", model.getaGrade());
        value.put("_aRecomment", model.getaRecomment());
        value.put("_aName", model.getaName());
        value.put("_aComments", model.getaComments());
        value.put("_aVisitors", model.getaVisitors());
        value.put("_aDown", model.getaDown());
        value.put("_aDesc", model.getaDesc());
        value.put("_aDwonloadUrl", model.getaDwonloadUrl());
        value.put("_packageName", model.getPackageName());
        value.put("_savePath", model.getSavePath());
        value.put("_active", model.getActive());
        value.put("_downloadSize", model.getDownloadSize());
        value.put("_appSize", model.getAppSize());
        value.put("_downloadStatus", model.getDownloadStatus());
        value.put("_openway", model.getOpenway());
        value.put("_aTitle", model.getaTitle());
        value.put("_advurl", model.getAdvurl());
        return value;
    }

    /**
     * JSON转对象
     */
    public static final List<AppModel> jsnToMdl(JSONArray array,int cateModel) {
        if (array == null || array.length() == 0) return null;
        List<AppModel> models = new ArrayList<>();
        try {
            for (int i = 0, leg = array.length(); i < leg; i++) {
                JSONObject item = array.getJSONObject(i);
                AppModel model = new AppModel();
                long aId;
                int aCateId;
                int aVersion;
                String aVersionName;
                String aUpdateTime;
                String aSize;
                String aContainSystem;
                String aLogo;
                String aGrade;
                String aRecomment;
                String aName;
                String aComments;
                String aVisitors;
                String aDown;
                String aDesc;
                String aDwonloadUrl;
                String packageName;
                int openway;
                String aTitle;
                String advurl;
                aId = item.optInt("appid");
                aCateId = cateModel;
                aVersion = item.optInt("app_ver_num");
                aVersionName = item.optString("app_version");
                aUpdateTime = item.optString("app_update_time");
                aSize = item.optString("app_size");
                aContainSystem = item.optString("app_system");
                aLogo = item.optString("logo");
                aGrade = item.optString("app_grade");
                aRecomment = item.optString("app_recomment");
                aName = item.optString("uname");
                aComments = item.optString("app_comments");
                aVisitors = item.optString("app_visitors");
                aDown = item.optString("app_down");
                aDesc = item.optString("app_desc");
                aDwonloadUrl = item.optString("down_url");
                packageName = item.optString("packagename");
                openway = item.optInt("openway");
                aTitle = item.optString("app_title");
                advurl=item.optString("ad_url");
                model.setaId(aId);
                model.setaCateId(aCateId);
                model.setaVersion(aVersion);
                model.setaVersionName(aVersionName);
                model.setaUpdateTime(aUpdateTime);
                model.setaSize(aSize);
                model.setaContainSystem(aContainSystem);
                model.setaLogo(aLogo);
                model.setaGrade(aGrade);
                model.setaRecomment(aRecomment);
                model.setaName(aName);
                model.setaComments(aComments);
                model.setaVisitors(aVisitors);
                model.setaDown(aDown);
                model.setaDesc(aDesc);
                model.setaDwonloadUrl(aDwonloadUrl);
                model.setPackageName(packageName);
                model.setOpenway(openway);
                model.setaTitle(aTitle);
                model.setSavePath(model.getGeneralPath());
                model.setAdvurl(advurl);
                models.add(model);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return models;
    }

    /**
     * 拉取当前正在下载和等待下载的总量
     */
    public static final int loadDownloadCount(ContentResolver contentResolver, boolean andPause) {
        if (contentResolver == null) return 0;
        Cursor cursor = null;
        if (andPause) {
            cursor = contentResolver.query(uri, new String[]{"count(*) as dcount"}, "_active=1 and (_downloadStatus=? or _downloadStatus=? or _downloadStatus=?)",
                    new String[]{String.valueOf(AppModel.DownloadStatus.DOWNLOAD_ING), String.valueOf(AppModel.DownloadStatus.DOWNLOAD_WAIT), String.valueOf(AppModel.DownloadStatus.DOWNLOAD_PAUSE)}, null);
        } else {

            cursor = contentResolver.query(uri, new String[]{"count(*) as dcount"}, "_active=1 and (_downloadStatus=? or _downloadStatus=?)",
                    new String[]{String.valueOf(AppModel.DownloadStatus.DOWNLOAD_ING), String.valueOf(AppModel.DownloadStatus.DOWNLOAD_WAIT)}, null);
        }
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * 拉取存在包名的数据
     */
    public static final List<AppModel> loadHasPkgNameData(ContentResolver contentResolver) {
        if (contentResolver == null) return null;
        Cursor cursor = contentResolver.query(uri, null, " _packageName is not null ", null, null);
        return csrToMdl(cursor, true);
    }

    /**
     * 将存在包名并且已安装的数据更新为已安装
     */
    public static final void updateInstallStatus(final ContentResolver contentResolver) {
        if (contentResolver == null) return;
        List<AppModel> models = loadHasPkgNameData(contentResolver);
        if (models != null) {
            for (final AppModel model : models) {
                if (AppUtil.isInstallPkg(AppConstant.app, model.getPackageName())) {
                    model.setDownloadStatus(AppModel.DownloadStatus.INSTALLED);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues values = AppModelUtil.mdlToCvs(model);
                            contentResolver.update(uri, values, " _id=? ", new String[]{String.valueOf(model.getId())});
                        }
                    });
                }
            }
        }
    }

    /**
     * 获悉当前的状态
     */
    public static final String getDownloadStatusStr(AppModel model) {
        if (model == null) return null;
        Resources resources = AppConstant.app.getResources();
        switch (model.getDownloadStatus()) {
            //下载完成
            case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                return resources.getString(R.string.download_finish);
            //下载暂停
            case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                return resources.getString(R.string.download_pause);
            //正在下载
            case AppModel.DownloadStatus.DOWNLOAD_ING:
                return resources.getString(R.string.download_ing);
            //下载错误
            case AppModel.DownloadStatus.DOWNLOAD_ERR:
                return resources.getString(R.string.download_err);
            //等待下载
            case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                return resources.getString(R.string.download_wait);
            //未下载
            case AppModel.DownloadStatus.DOWNLOAD_NOT:
                return resources.getString(R.string.download_game);
            //已安装
            case AppModel.DownloadStatus.INSTALLED:
                return resources.getString(R.string.download_install);
            //升级
            case AppModel.DownloadStatus.UPGRADE:
                return resources.getString(R.string.update);
        }
        return null;
    }
}
