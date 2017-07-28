package com.zwhd.appbase.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YI on 2015/12/12.
 */
public class GamePageListAdapter extends ArrayAdapter<AppModel> implements View.OnClickListener {
    BaseActivity activity;
    GamePageListHolder holder;
    AppModelDownloadPool appModelDownloadPool = AppModelDownloadPool.getInstance();

    public GamePageListAdapter(Context context, List<AppModel> objects) {
        super(context, -1, objects);
        this.activity = (BaseActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = ViewUtil.inflate(activity, R.layout.game_list_item);
            holder = new GamePageListHolder(view);
            view.setTag(holder);
        } else {
            holder = (GamePageListHolder) view.getTag();
        }
        AppModel model = getItem(position);
        ImageLoader.getInstance().displayImage(String.valueOf(model.getaLogo()), holder.game_pic);
        ViewUtil.setContent(holder.gamehow_txt, String.valueOf(model.getaSize()));
        ViewUtil.setContent(holder.gamename_txt, String.valueOf(model.getaTitle()));
        holder.game_doawload_btn.setBackgroundResource(R.drawable.round_bule_white_bg);
        holder.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._03a9f3));
        String str = null;
        //下载按钮状态
        switch (model.getDownloadStatus()) {
            //下载完成
            case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                holder.game_doawload_btn.setBackgroundResource(R.drawable.round_green_white_bg);
                holder.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._01ed44));
                str = activity.resources.getString(R.string.download_finish);
                break;
            //下载暂停
            case AppModel.DownloadStatus.DOWNLOAD_PAUSE:
                str = activity.resources.getString(R.string.download_pause);
                break;
            //正在下载
            case AppModel.DownloadStatus.DOWNLOAD_ING:
                str = activity.resources.getString(R.string.download_ing);
                break;
            //下载错误
            case AppModel.DownloadStatus.DOWNLOAD_ERR:
                str = activity.resources.getString(R.string.download_err);
                break;
            //等待下载
            case AppModel.DownloadStatus.DOWNLOAD_WAIT:
                str = activity.resources.getString(R.string.download_wait);
                break;
            //未下载
            case AppModel.DownloadStatus.DOWNLOAD_NOT:
                str = activity.resources.getString(R.string.download_game);
                break;
            //已安装
            case AppModel.DownloadStatus.INSTALLED:
                holder.game_doawload_btn.setBackgroundResource(R.drawable.round_gray_bg);
                holder.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._b6b6b6));
                str = activity.resources.getString(R.string.download_install);
                break;
            //升级
            case AppModel.DownloadStatus.UPGRADE:
                str = activity.resources.getString(R.string.update);
                break;
        }
        holder.game_doawload_btn.setText(str);
        holder.game_doawload_btn.setTag(R.id.bundle_params, position - 1);
        Map<String, Object> map = new HashMap<>();
        map.put("a", holder.game_doawload_btn);
        holder.game_doawload_btn.setTag(map);
        holder.game_doawload_btn.setOnClickListener(this);
        AppModelDownloadPool.attachViewToDownload(model, holder.game_doawload_btn, null);


        return view;
    }

    @Override
    public void onClick(View v) {
        int position = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
        AppModel appModel = getItem(position);
        if (appModel.getOpenway() == AppModel.OpenWay.BROSWER) {//浏览器打开方式
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(appModel.getaDwonloadUrl()));
            activity.startActivity(intent);
            return;
        }
        switch (appModel.getDownloadStatus()) {
            //下载完成
            case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                File f = new File(appModel.getSavePath());
                if (AppUtil.fileHasValue(f.getAbsolutePath())) {//打开安装
                    AppUtil.installApp(activity, f);
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
        Map<String, Object> map = (Map<String, Object>) v.getTag();
        AppModelDownloadPool.attachViewToDownload(appModel, (TextView) map.get("a"), null);
    }

    final static class GamePageListHolder {
        /**
         * 游戏图片
         */
        ImageView game_pic;
        /**
         * 游戏名称
         */
        TextView gamename_txt;
        /**
         * 游戏大小
         */
        TextView gamehow_txt;
        /**
         * 游戏下载按钮
         */
        Button game_doawload_btn;

        public GamePageListHolder(View itemView) {
            game_pic = ViewUtil.findViewById(itemView, R.id.game_pic);
            gamehow_txt = ViewUtil.findViewById(itemView, R.id.game_how_txt);
            game_doawload_btn = ViewUtil.findViewById(itemView, R.id.download_btn);
            gamename_txt = ViewUtil.findViewById(itemView, R.id.game_name_txt);
        }
    }
}
