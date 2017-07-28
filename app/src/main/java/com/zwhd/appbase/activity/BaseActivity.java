package com.zwhd.appbase.activity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;
import com.zwhd.appbase.App;
import com.zwhd.appbase.R;
import com.zwhd.appbase.http.BaseHttpHandler;
import com.zwhd.appbase.http.HttpHandler;
import com.zwhd.appbase.http.HttpResponseModel;

/**
 * Created by zaoxin on 2015/12/8.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener, HttpHandler {
    public Bundle bundle;
    /**
     * 屏幕显示信息
     */
    public DisplayMetrics displayMetrics = new DisplayMetrics();
    /**
     * 上下文
     */
    public Context context;
    public Resources resources;
    /**
     * 请求回调
     */
    public BaseHttpHandler httpHandler = new BaseHttpHandler(this) {
    };
    /**
     * 应用
     */
    public App app;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        bundle = getIntent().getExtras();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        resources = getResources();
        context = this;
        app = new App();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void success(HttpResponseModel message) throws Exception {

    }

    @Override
    public void error(HttpResponseModel message) throws Exception {

    }

    @Override
    public void httpErr() throws Exception {

    }

    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {

    }
}
