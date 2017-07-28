package com.zwhd.appbase.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.IUrconstant;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.http.BaseHttpHandler;
import com.zwhd.appbase.http.HttpHandler;
import com.zwhd.appbase.http.HttpResponseModel;
import com.zwhd.appbase.util.AppUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 下载完成上报的广播
 *
 * @author YI
 */
public class DownloadSuccessReciver extends BroadcastReceiver implements HttpHandler {
    public DownloadSuccessReciver() {
    }

    BaseHttpHandler handler = new BaseHttpHandler(this) {
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(AppConstant.APP_DOWNLOAD_SUCCESS_ACTION)) {//本地的下载完成
            try {
                AppModel model = (AppModel) intent.getSerializableExtra(IConstant.BUNDLE_PARAMS);
                Map<String, String> params = new HashMap<>();
                params.put("imei", AppUtil.getImei(context));
                params.put("packagename", AppUtil.loadFilePkgName(context, new File(model.getSavePath())));
                params.put("channel", AppUtil.readMetaDataByName(context, "UMENG_CHANNEL"));
                IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.DOWNLOAD_SUCCESS_UPLOAD_URL, params, handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
