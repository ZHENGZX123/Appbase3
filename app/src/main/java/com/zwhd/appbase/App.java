package com.zwhd.appbase;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.zwhd.advsdk.GameFlyAd;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModelUtil;

/**
 * Created by zaoxin on 2015/12/9.
 */
public class App extends Application {
    /**
     * Hot长度
     * */
    public int size;

    @Override
    public void onCreate() {
        super.onCreate();
        AppConstant.app = this;
        DisplayImageOptions options = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisk(true)
                .showImageForEmptyUri(R.mipmap.ic_launcher).
                        showImageOnFail(R.mipmap.ic_launcher)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2).resetViewBeforeLoading(true)
                .cacheInMemory(true).considerExifParams(true).build();
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(this);
        config.threadPriority(Thread.NORM_PRIORITY - 3);
        config.denyCacheImageMultipleSizesInMemory();
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.defaultDisplayImageOptions(options);
        com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(config.build());
        GameFlyAd.init(this, 10000, "1cd56fa52af6140c1e65eebd7e1752bc");
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
