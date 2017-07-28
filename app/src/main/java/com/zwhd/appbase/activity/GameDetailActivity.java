package com.zwhd.appbase.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.IUrconstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.download.AppModelUtil;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zaoxin on 2015/12/14.
 */
public class GameDetailActivity extends BaseActivity {
    AppModel appModel;
    ImageView game_pic;
    Button download_btn;
    String str;
    WebView webView;
    Handler handler;
    TextView game_size_txt, game_intro_txt, game_update_txt;
    AppModelDownloadPool appModelDownloadPool = AppModelDownloadPool.getInstance();
    AppDownloadCompleteReciver appDownloadCompleteReciver = new AppDownloadCompleteReciver();
    IntentFilter filter = new IntentFilter();

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.acitivty_game_detail);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                try {
                    appModel = (AppModel) AppModelUtil.queryByCateIdandAid(getContentResolver(), appModel.getaCateId(), appModel.getaId());
                    loadData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        Bundle bundle = this.getIntent().getExtras();
        appModel = (AppModel) bundle.getSerializable(IConstant.BUNDLE_PARAMS);
        game_pic = ViewUtil.findViewById(this, R.id.game_pic);
        download_btn = ViewUtil.findViewById(this, R.id.download_btn);
        findViewById(R.id.previour).setOnClickListener(this);
        findViewById(R.id.download_btn).setOnClickListener(this);
        game_size_txt = ViewUtil.findViewById(this, R.id.game_size);
        game_intro_txt = ViewUtil.findViewById(this, R.id.game_intro);
        game_update_txt = ViewUtil.findViewById(this, R.id.game_update);
        webView = ViewUtil.findViewById(this, R.id.game_web);
        try {
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        filter.addAction(AppConstant.APP_DOWNLOAD_SUCCESS_ACTION);
        registerReceiver(appDownloadCompleteReciver, filter);
    }

    void loadData() throws Exception {
        if (appModel == null)
            return;
        ViewUtil.setContent(this, R.id.game_size, StringUtil.format(resources.getString(R.string.game_size), appModel.getaSize()));
        ViewUtil.setTextFontColor(game_size_txt, null, resources.getColor(R.color._666666), 0, 5);
        ViewUtil.setContent(this, R.id.game_intro, StringUtil.format(resources.getString(R.string.game_intro), appModel.getaContainSystem()));
        ViewUtil.setTextFontColor(game_intro_txt, null, resources.getColor(R.color._666666), 0, 6);
        ViewUtil.setContent(this, R.id.game_download_number, StringUtil.format(resources.getString(R.string.game_down_number), appModel.getaDown()));
        ViewUtil.setContent(this, R.id.game_update, StringUtil.format(resources.getString(R.string.game_update),
                StringUtil.getDateField(Long.parseLong(appModel.getaUpdateTime()) * 1000, 6)));
        ViewUtil.setTextFontColor(game_update_txt, null, resources.getColor(R.color._666666), 0, 7);
        ViewUtil.setContent(this, R.id.game_detaol_title, appModel.getaTitle());
        ImageLoader.getInstance().displayImage(appModel.getaLogo(), game_pic);
        //下载按钮状态
        switch (appModel.getDownloadStatus()) {
            //下载完成
            case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                download_btn.setBackgroundResource(R.drawable.round_green_white_bg);
                download_btn.setTextColor(resources.getColor(R.color._01ed44));
                break;
            case AppModel.DownloadStatus.INSTALLED:
                download_btn.setBackgroundResource(R.drawable.round_gray_bg);
                download_btn.setTextColor(resources.getColor(R.color._b6b6b6));
                break;

        }
        download_btn.setText(AppModelUtil.getDownloadStatusStr(appModel));
        AppModelDownloadPool.attachViewToDownload(appModel,download_btn, null);
        /**
         * 网页
         * */
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(false);
        webView.loadUrl(IUrconstant.GAME_DETAIL_URL + appModel.getaId());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.previour:
                finish();
                break;
            case R.id.download_btn:
                switch (appModel.getDownloadStatus()) {
                    //下载完成
                    case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                        File f = new File(appModel.getSavePath());
                        if (AppUtil.fileHasValue(f.getAbsolutePath())) {//打开安装
                            AppUtil.installApp(this, f);
                        } else {//重新下载
                            try {
                                AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    //下载暂停
                    case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                        try {
                            appModelDownloadPool.addDownload(AppConstant.app.getContentResolver(), appModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //正在下载
                    case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                    case AppModel.DownloadStatus.DOWNLOAD_ING:
                        AppModelDownloadPool.pauseById(AppConstant.app.getContentResolver(), appModel.getId());
                        break;
                    //下载错误
                    case AppModel.DownloadStatus.DOWNLOAD_ERR:
                        try {
                            appModelDownloadPool.addDownload(AppConstant.app.getContentResolver(), appModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //未下载
                    case AppModel.DownloadStatus.DOWNLOAD_NOT:
                        try {
                            AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //已安装
                    case AppModel.DownloadStatus.INSTALLED:
                        break;
                    //升级
                    case AppModel.DownloadStatus.UPGRADE:
                        break;
                }
                AppModelDownloadPool.attachViewToDownload(appModel,download_btn, null);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
            unregisterReceiver(appDownloadCompleteReciver);
    }

    /**
     * 下载完成监听
     */
    class AppDownloadCompleteReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            handler.sendEmptyMessage(0);
        }
    }
}
