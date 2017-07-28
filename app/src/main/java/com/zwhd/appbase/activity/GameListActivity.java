package com.zwhd.appbase.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.allen.expandablelistview.SwipeMenuExpandableCreator;
import com.allen.expandablelistview.SwipeMenuExpandableListView;
import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.zwhd.appbase.R;
import com.zwhd.appbase.adapter.GameListAdapter;
import com.zwhd.appbase.dialog.ConfirmDialog;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.download.AppModelDownloadStatusObserver;
import com.zwhd.appbase.download.AppModelProvider;
import com.zwhd.appbase.download.AppModelUtil;
import com.zwhd.appbase.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;


public class GameListActivity extends BaseActivity implements SwipeMenuExpandableCreator, SwipeMenuExpandableListView.OnMenuItemClickListenerForExpandable, ConfirmDialog.ConfirmCallBack {
    SwipeMenuExpandableListView listView;
    GameListAdapter adapter;
    Uri uri = Uri.parse(AppModelProvider.APPS_URI);
    AppModelDownloadStatusObserver statusObserver;
    Handler handler;
    ConfirmDialog confirmDialog;
    AppDownloadCompleteReciver appDownloadCompleteReciver = new AppDownloadCompleteReciver();
    IntentFilter filter = new IntentFilter();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        listView = ViewUtil.findViewById(this, R.id.game_list_item);
        adapter = new GameListAdapter(this, new ArrayList<AppModel>(), new ArrayList<AppModel>());
        confirmDialog = new ConfirmDialog(this, this);
        listView.setAdapter(adapter);
        listView.setMenuCreator(this);
        listView.setOnMenuItemClickListener(this);
        findViewById(R.id.previour).setOnClickListener(this);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                List<AppModel> models = AppModelUtil.queryInActive(getContentResolver());
                List<AppModel>ing =new ArrayList<>();
                List<AppModel>fin=new ArrayList<>();
                for (AppModel appModel : models) {
                    if (appModel.getDownloadStatus() == AppModel.DownloadStatus.DOWNLOAD_COMPLETE)
                       fin.add(appModel);
                    else
                        ing.add(appModel);
                }
                if ((ing.size()!=adapter.modelsing.size())||(fin.size()!=adapter.modelsfinish.size())) {
                    adapter.modelsfinish.clear();
                    adapter.modelsing.clear();
                    adapter.modelsfinish.addAll(fin);
                    adapter.modelsing.addAll(ing);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        handler.sendEmptyMessage(0);
        for (int i = 0; i < 2; i++) {
            listView.expandGroup(i);

        }
        filter.addAction(AppConstant.APP_DOWNLOAD_SUCCESS_ACTION);
        filter.addAction(AppConstant.APP_DOWNLOAD_STATUS_UPDATE_ACTION);
        registerReceiver(appDownloadCompleteReciver, filter);

        //statusObserver = new AppModelDownloadStatusObserver(handler);
        //getContentResolver().registerContentObserver(uri, true, statusObserver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //getContentResolver().unregisterContentObserver(statusObserver);
        unregisterReceiver(appDownloadCompleteReciver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.previour:
                finish();
                break;
        }
    }

    @Override
    public void createGroup(SwipeMenu menu) {
    }

    @Override
    public void createChild(SwipeMenu menu) {
        SwipeMenuItem item = new SwipeMenuItem(getApplicationContext());
        item.setBackground(new ColorDrawable(Color.rgb(0xf7, 0xf7, 0xf7)));
        item.setWidth((int) getResources().getDimension(R.dimen._70dp));
        item.setIcon(R.drawable.delete_selecter);
        menu.addMenuItem(item);
    }


    @Override
    public boolean onMenuItemClick(int groupPosition, int childPosition, SwipeMenu menu, int index) {
        switch (index) {
            case 0:
                listView.getTouchView().closeMenu();
                if (confirmDialog != null && !confirmDialog.isShowing()) {
                    confirmDialog.model = adapter.getChild(groupPosition, childPosition);
                    confirmDialog.show();
                }
                break;
        }
        return false;
    }

    @Override
    public void ok(Dialog dialog, AppModel model) {
        dialog.dismiss();
        if (model != null) {
            model.setDownloadStatus(AppModel.DownloadStatus.DOWNLOAD_PAUSE);
            AppModelDownloadPool.pauseById(getContentResolver(), model.getId());
            AppModelUtil.deleteDownload(getContentResolver(), model);
            adapter.modelsing.remove(model);
            adapter.modelsfinish.remove(model);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void cancle(Dialog dialog, AppModel model) {
        dialog.dismiss();
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
