package com.zwhd.appbase.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.Html;

import com.google.protobuf.ByteString;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;


/**
 * 应用工具集
 *
 * @author YI
 */
public class AppUtil {
    public static DisplayImageOptions getOptions(boolean reset) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).resetViewBeforeLoading(reset).cacheInMemory(true).considerExifParams(true).build();
        return options;
    }

    /**
     * 网络是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * 调用图像截图
     */
    public static void cropImage(Uri picUri, Uri outUri, int requestCode, Activity activity) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("outputX", 500);
            cropIntent.putExtra("outputY", 500);
            cropIntent.putExtra("return-data", false);
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);
            activity.startActivityForResult(cropIntent, requestCode);
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
        }
    }

    /**
     * 文件转化为byte[]
     */
    public static byte[] fileToBytes(File file) {
        if (file == null || file.isDirectory() || !file.exists())
            return null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] b = new byte[fis.available()];
            fis.read(b);
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fis != null)
                try {
                    fis.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
        }
        return null;
    }

    /**
     * 创建软件的私有文件夹
     */
    public static void createFloder() {

        File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
        if (!file.exists())
            file.mkdirs();
        File f = new File(file, IConstant.CAMERA_PHOTO_FLODER);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.DOWNLOAD_PHOTO_FLODER);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.DOWNLOAD_AUDIO_FLODER);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.RECORDER_AUDIO_FLODER);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.DOWNLOAD_FILES);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.RECORDER_VIDEO_FLODER);
        if (!f.exists())
            f.mkdirs();
        f = new File(file, IConstant.DOWNLOAD_VIDEO_FLODER);
        if (!f.exists())
            f.mkdirs();
    }

    /**
     * 清空缓存
     */
    public static void clearCache(boolean isDeleteFloder) {
        try {
            deleteFiles(new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT).getAbsolutePath(),
                    isDeleteFloder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空我们的文件
     */
    public static void deleteFiles(String filePath, boolean isDeleteFloder) throws Exception {
        File file = new File(filePath);
        if (!file.exists())
            return;
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFiles(f.getAbsolutePath(), isDeleteFloder);
            }
            if (isDeleteFloder)
                file.delete();
        }
    }

    /**
     * 删除更新下载目录文件
     */

    public static String deleteDownload() {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.DOWNLOAD_FILES);
            File[] files = file.listFiles();
            for (File f : files) {
                if (!f.isDirectory())
                    f.delete();
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 创建新的音频
     */
    public static String createNewAudio() {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.RECORDER_AUDIO_FLODER);
            file = new File(file, System.currentTimeMillis() + "_audio");
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 下载音频
     */
    public static String downloadAudio(Object object) {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.DOWNLOAD_AUDIO_FLODER);
            file = new File(file, StringUtil.MD5(object.toString()));
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 创建新的视频
     */
    public static String createNewVideo() {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.RECORDER_VIDEO_FLODER);
            file = new File(file, System.currentTimeMillis() + "_video");
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 下载音频
     */
    public static String downloadVideo(Object object) {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.DOWNLOAD_VIDEO_FLODER);
            file = new File(file, StringUtil.MD5(object.toString()));
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 创建新的相片
     */
    public static String createNewPhoto() {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.CAMERA_PHOTO_FLODER);
            file = new File(file, System.currentTimeMillis() + "_img");
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 下载相片
     */
    public static String downloadPhoto(Object object) {
        try {
            createFloder();
            File file = new File(Environment.getExternalStorageDirectory(), IConstant.ZWHD_ROOT);
            file = new File(file, IConstant.DOWNLOAD_PHOTO_FLODER);
            file = new File(file, object.toString());
            return file.getAbsolutePath();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取截取的图像数据
     */
    public static Bitmap getBitmapForCrop(Intent data, String picturePath) {
        Bitmap bitmap = null;
        try {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null)
                    bitmap = extras.getParcelable("data");
            }
            if (bitmap == null) {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(picturePath);
                    bitmap = BitmapFactory.decodeStream(fis);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 文件能转化为二进制数组
     */
    public static byte[] fileToBytes(String path) {
        return fileToBytes(new File(path));
    }


    /**
     * 检测是否安装谷歌服务
     */
    public static boolean isGooglePlayInstalled(Context context) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            PackageInfo info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES);
            String label = (String) info.applicationInfo.loadLabel(pm);
            app_installed = (label != null && !label.equals("Market"));
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    /**
     * 生成指定范围的随机整形数
     */
    public static int randomInt(int min, int max) {
        if (max <= min)
            return min;
        return min + (int) ((double) (max - min) * Math.random());
    }

    /**
     * 检测文件是否有内容
     */
    public final static boolean fileHasValue(String path) {
        if (StringUtil.isEmpty(path))
            return false;
        File file = new File(path);
        if (!file.exists())
            return false;
        if (file.length() <= 0)
            return false;
        return true;
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 文件下载
     */
    public static void downloadFile(File f, String path) {
        if (!f.exists()) {
            HttpURLConnection con = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;
            BufferedInputStream bis = null;
            try {
                URL url = new URL(path);
                con = (HttpURLConnection) url.openConnection();
                con.setConnectTimeout(60 * 1000);
                con.setReadTimeout(60 * 1000);
                if (con != null && con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    bis = new BufferedInputStream(con.getInputStream());
                    fos = new FileOutputStream(f);
                    bos = new BufferedOutputStream(fos);
                    byte[] b = new byte[1024];
                    int length;
                    while ((length = bis.read(b)) != -1) {
                        bos.write(b, 0, length);
                        bos.flush();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bos != null)
                        bos.close();
                    if (fos != null)
                        fos.close();
                    if (bis != null)
                        bis.close();
                    if (con != null)
                        con.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 创建通知
     *
     * @param context 上下文
     * @param title   通知标题
     * @param msg     通知消息
     * @param intent  通知点击进入的界面
     * @param flag    设置的通知ID
     */
    public final static void createNotifaction(Context context, String title, String msg, Intent intent, int flag) {
        if (context == null) return;
        if (intent == null) return;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setContentInfo(msg)
                .setContentTitle(title).setContentText(Html.fromHtml(msg)).setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(PendingIntent.getActivity(context, 1, intent, flag)).build();
        notificationManager.notify(flag, notification);

    }

    /**
     * Bitmap转换为ByteString
     */
    public final static ByteString bmpToBst(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteString byteString;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byteString = ByteString.copyFrom(outputStream.toByteArray());
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteString;
    }

    /**
     * ByteString转换为Bitmap
     */
    public final static Bitmap bstToBmp(ByteString byteString) {
        if (byteString == null) return null;
        InputStream inputStream = new ByteArrayInputStream(byteString.toByteArray());
        if (inputStream == null) return null;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 应用是否已安装
     */
    public final static boolean isInstallPkg(Context context, String pkgName) {
        PackageInfo packageInfo = null;
        if (StringUtil.isEmpty(pkgName)) return false;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pkgName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
            e.printStackTrace();
        }
        if (packageInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 启动APP
     */
    public final static void startAPP(Context context, String pkgName) {
        if (StringUtil.isEmpty(pkgName)) return;
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装应用
     */
    public final static void installApp(Context context, File f) {
        if (f == null || !f.exists()) return;
        try {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(install);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取IMEI
     */
    public final static String getImei(Context context) {
        String imei = "";
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (imei == null || imei.length() == 0)
            imei = MD5(Build.HOST + Build.BRAND + Build.VERSION.RELEASE + context.getPackageName());
        return imei;
    }

    /**
     * MD5加密
     */
    public final static String MD5(String str) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte messageDigest[] = digest.digest();
            return toHexString(messageDigest);
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private final static char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final static String toHexString(byte[] b) { // String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 读取文件的包名
     */
    public final static String loadFilePkgName(Context context, File file) {
        if (context == null) return null;
        if (file == null || !file.exists()) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(file.getAbsolutePath(), 0);
            if (info != null) return info.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取META-DATA数据
     */
    public final static String readMetaDataByName(Context context, String key) {
        if (context == null) return null;
        if (StringUtil.isEmpty(key)) return null;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            return appInfo.metaData.getString(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
