package com.zwhd.appbase.adapter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.allen.expandablelistview.BaseSwipeMenuExpandableListAdapter;
import com.baoyz.swipemenulistview.ContentViewWrapper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.App;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;


import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zaoxin on 2015/12/10.
 */
public class GameListAdapter extends BaseSwipeMenuExpandableListAdapter implements View.OnClickListener {
    /**
     * 进行中
     */
    public List<AppModel> modelsing;
    /**
     * 已完结
     */
    public List<AppModel> modelsfinish;
    GameListHolder gameListHolder;
    GameChildHoder gameChildHoder;
    BaseActivity activity;
    NumberFormat numberFormat = NumberFormat.getInstance();
    String string;

    public GameListAdapter(Context activity, List<AppModel> modelsing, List<AppModel> modelsfinish) {
        this.activity = (BaseActivity) activity;
        this.modelsing = modelsing;
        this.modelsfinish = modelsfinish;
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (groupPosition == 0)
            return modelsing.size();
        else
            return modelsfinish.size();
    }

    @Override
    public String getGroup(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return activity.resources.getString(R.string.download_ing);
            case 1:
                return activity.resources.getString(R.string.download_finish);
        }
        return null;
    }

    @Override
    public AppModel getChild(int groupPosition, int childPosition) {
        if (groupPosition == 0)
            return modelsing.get(childPosition);
        else
            return modelsfinish.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return getChild(groupPosition, childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isGroupSwipable(int groupPosition) {
        return true;
    }

    @Override
    public boolean isChildSwipable(int groupPosition, int childPosition) {
        return true;
    }


    @Override
    public ContentViewWrapper getGroupViewAndReUsable(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        boolean bl = true;
        View view = convertView;
        if (view == null) {
            view = View.inflate(activity, R.layout.head_download_list_item, null);
            gameListHolder = new GameListHolder();
            gameListHolder.game_name_staust = ViewUtil.findViewById(view, R.id.game_stutes);
            gameListHolder.game_check = ViewUtil.findViewById(view, R.id.game_dawnload_check_two);
            view.setTag(gameListHolder);
            bl = false;
        } else {
            gameListHolder = (GameListHolder) view.getTag();
            gameListHolder.game_name_staust.setText(getGroup(groupPosition));
        }
        gameListHolder.game_check.setActivated(isExpanded);
        return new ContentViewWrapper(view, bl);
    }

    @Override
    public ContentViewWrapper getChildViewAndReUsable(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        boolean bl = true;
        View view = convertView;
        if (view == null) {
            gameChildHoder = new GameChildHoder();
            view = View.inflate(activity, R.layout.download_list_item, null);
            gameChildHoder.game_img = ViewUtil.findViewById(view, R.id.game_pic);
            gameChildHoder.download_btn = ViewUtil.findViewById(view, R.id.download_btn);
            gameChildHoder.game_name = ViewUtil.findViewById(view, R.id.game_name);
            gameChildHoder.download_txt = ViewUtil.findViewById(view, R.id.game_dawnload_p);
            gameChildHoder.game_sek = ViewUtil.findViewById(view, R.id.item_progress);
            view.setTag(gameChildHoder);
            bl = false;
        } else {
            gameChildHoder = (GameChildHoder) view.getTag();
        }
        AppModel appModel = getChild(groupPosition, childPosition);
        gameChildHoder.game_name.setText(appModel.getaTitle());
        ImageLoader.getInstance().displayImage(String.valueOf(appModel.getaLogo()), gameChildHoder.game_img);
        int progress = (int) ((float) appModel.getDownloadSize() / (float) appModel.getAppSize() * 100);
        if(progress>100||progress<0)progress=0;
        gameChildHoder.download_txt.setText(progress + "%");
        gameChildHoder.game_sek.setProgress(progress);
        gameChildHoder.download_btn.setBackgroundResource(R.drawable.round_bule_white_bg);
        gameChildHoder.download_btn.setTextColor(activity.resources.getColor(R.color._03a9f3));
        switch (appModel.getDownloadStatus()) {
            //下载完成
            case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                gameChildHoder.download_btn.setBackgroundResource(R.drawable.round_green_white_bg);
                gameChildHoder.download_btn.setTextColor(activity.resources.getColor(R.color._01ed44));
                string = activity.resources.getString(R.string.download_finish);
                break;
            //下载暂停
            case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                string = activity.resources.getString(R.string.download_pause);
                break;
            //正在下载
            case AppModel.DownloadStatus.DOWNLOAD_ING:
                string = activity.resources.getString(R.string.download_ing);
                break;
            //下载错误
            case AppModel.DownloadStatus.DOWNLOAD_ERR:
                string = activity.resources.getString(R.string.download_err);
                break;
            //等待下载
            case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                string = activity.resources.getString(R.string.download_wait);
                break;
            //未下载
            case AppModel.DownloadStatus.DOWNLOAD_NOT:
                string = activity.resources.getString(R.string.download_game);
                break;
            //已安装
            case AppModel.DownloadStatus.INSTALLED:
                gameChildHoder.download_btn.setBackgroundResource(R.drawable.round_gray_bg);
                gameChildHoder.download_btn.setTextColor(activity.resources.getColor(R.color._b6b6b6));
                string = activity.resources.getString(R.string.download_install);
                break;
            //升级
            case AppModel.DownloadStatus.UPGRADE:
                string = activity.resources.getString(R.string.update);
                break;
        }
        gameChildHoder.download_btn.setText(string);
        gameChildHoder.download_btn.setTag(R.id.bundle_params, groupPosition);
        gameChildHoder.download_btn.setTag(R.id.bundle_params1, childPosition);
        gameChildHoder.download_btn.setOnClickListener(this);
        Map<String,Object> map=new HashMap<>();
        map.put("a",gameChildHoder.download_btn);
        map.put("b",gameChildHoder.download_txt);
        map.put("c",gameChildHoder.game_sek);
        gameChildHoder.download_btn.setTag(map);
        AppModelDownloadPool.attachViewToDownload(appModel,gameChildHoder.download_btn,gameChildHoder.download_txt,gameChildHoder.game_sek);
        return new ContentViewWrapper(view, bl);
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public void onClick(View v) {
        int position = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
        int gropPosition=StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
        int childPosition=StringUtil.toInt(v.getTag(R.id.bundle_params1).toString());
        AppModel appModel =getChild(gropPosition,childPosition);
        switch (v.getId()) {
            case R.id.download_btn:
                Map<String,Object> map= (Map<String, Object>) v.getTag();
                switch (appModel.getDownloadStatus()) {
                    //下载完成
                    case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                        File f = new File(appModel.getSavePath());
                        if (AppUtil.fileHasValue(f.getAbsolutePath())) {
                            AppUtil.installApp(activity, f);
                        }else{
                            try {
                                AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                                AppModelDownloadPool.attachViewToDownload(appModel, (TextView) map.get("a"), (TextView) map.get("b"), (ProgressBar) map.get("c"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    //下载暂停
                    case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                        try {
                            AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                            AppModelDownloadPool.attachViewToDownload(appModel, (TextView) map.get("a"), (TextView) map.get("b"), (ProgressBar) map.get("c"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //正在下载/等待下载
                    case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                    case AppModel.DownloadStatus.DOWNLOAD_ING:
                        AppModelDownloadPool.attachViewToDownload(appModel,(TextView)map.get("a"),(TextView)map.get("b"),(ProgressBar)map.get("c"));
                        AppModelDownloadPool.getInstance().pauseById(AppConstant.app.getContentResolver(), appModel.getId());
                        break;
                    //下载错误
                    case AppModel.DownloadStatus.DOWNLOAD_ERR:
                        try {
                            AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                            AppModelDownloadPool.attachViewToDownload(appModel, (TextView) map.get("a"), (TextView) map.get("b"), (ProgressBar) map.get("c"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //未下载
                    case AppModel.DownloadStatus.DOWNLOAD_NOT:
                        try {
                            AppModelDownloadPool.getInstance().addDownload(AppConstant.app.getContentResolver(), appModel);
                            AppModelDownloadPool.attachViewToDownload(appModel, (TextView) map.get("a"), (TextView) map.get("b"), (ProgressBar) map.get("c"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    //已安装
                    case AppModel.DownloadStatus.INSTALLED:
                        AppUtil.startAPP(activity, appModel.getPackageName());
                        break;
                    //升级
                    case AppModel.DownloadStatus.UPGRADE:
                        break;
                }


                break;

        }
    }

    /**
     * 正在下载
     */
    final static class GameListHolder {
        /**
         * 游戏正在下载
         */
        TextView game_name_staust;
        /**
         * 游戏展开隐藏
         */
        View game_check;
    }

    /**
     * 已下载
     */
    final static class GameChildHoder {
        /**
         * 游戏图片
         */
        ImageView game_img;
        /**
         * 游戏名字
         */
        TextView game_name;
        /**
         * 游戏下载百分比
         */
        TextView download_txt;
        /*
        * 游戏下载按钮
        * */
        Button download_btn;
        /**
         * 游戏下载进度条
         */
        ProgressBar game_sek;
    }
}
