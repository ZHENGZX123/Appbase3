package com.zwhd.appbase.download;

import android.database.ContentObserver;
import android.os.Handler;

/**
 * Created by YI on 2015/12/11.
 */
public class AppModelDownloadStatusObserver extends ContentObserver {
    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    Handler handler;

    public AppModelDownloadStatusObserver(Handler handler) {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        if (handler != null) handler.sendEmptyMessage(0);
    }
}
