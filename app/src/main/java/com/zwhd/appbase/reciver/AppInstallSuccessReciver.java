package com.zwhd.appbase.reciver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.IUrconstant;
import com.zwhd.appbase.http.BaseHttpConnectPool;
import com.zwhd.appbase.http.BaseHttpHandler;
import com.zwhd.appbase.http.HttpHandler;
import com.zwhd.appbase.http.HttpResponseModel;
import com.zwhd.appbase.util.AppUtil;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分墙应用安装拦截
 *
 * @author YI
 */
public final class AppInstallSuccessReciver extends BroadcastReceiver implements HttpHandler {
    final BaseHttpHandler handler = new BaseHttpHandler(this) {
    };
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action != null && action.startsWith(Intent.ACTION_PACKAGE_ADDED)) {
            String pkgName = intent.getData().getSchemeSpecificPart();
            AppUtil.startAPP(context, pkgName);
            Map<String, String> params = new HashMap<>();
            params.put("imei", AppUtil.getImei(context));
            params.put("packagename", pkgName);
            params.put("channel", AppUtil.readMetaDataByName(context, "UMENG_CHANNEL"));
            IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.INSTALL_SUCCESS_UPLOAD_URL, params, handler);
        }
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
