package com.zwhd.appbase.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.activity.GameDetailActivity;
import com.zwhd.appbase.activity.GameListActivity;
import com.zwhd.appbase.activity.MainActivity;
import com.zwhd.appbase.download.AdvModel;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.download.AppModelUtil;
import com.zwhd.appbase.download.CateModel;
import com.zwhd.appbase.util.AppUtil;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zaoxin on 2015/12/1.
 */
public class GameShowAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_VIEW_TYPE_BOTTOM = 2;
    public List<AppModel> labels;
    public List<AppModel> advModels;
    public int size_leng;
    BaseActivity activity;
    AppModelDownloadPool appModelDownloadPool = AppModelDownloadPool.getInstance();
    CateModel cate;
    List<RadioButton> buttons = new ArrayList<>();
    RelativeLayout.LayoutParams params, params1;

    public GameShowAdapter(BaseActivity activity, CateModel cate, List<AppModel> list, List<AppModel> advModels) {
        this.labels = list;
        this.activity = activity;
        this.cate = cate;
        this.advModels = advModels;
        float w = activity.displayMetrics.widthPixels;
        params = new RelativeLayout.LayoutParams((int) w, (int) (w / 2));
        params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0, (int) (params.height - activity.resources.getDimension(R.dimen._40dp)), 0, 0);
        params1.addRule(RelativeLayout.CENTER_HORIZONTAL);

    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new AdvHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_top, parent, false));
        } else if (viewType == ITEM_VIEW_TYPE_BOTTOM) {
            return new NewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_bottom, parent, false));
        }
        return new GameShowHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.game_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isHeader(position)) {
            AdvHolder h = (AdvHolder) holder;
            if (advModels != null && advModels.size() > 0) {
                h.gameTitle.setText(cate.getcName());
                h.viewPager.setLayoutParams(params);
                h.viewPager.setAdapter(new AdvAdapter(activity, advModels, buttons));
                h.rb_g.setLayoutParams(params1);
                h.rb_g.setEnabled(false);
                h.viewPager.setOnPageChangeListener(this);
                buttons.clear();
                for (int i = 0, leg = h.rb_g.getChildCount(); i < leg; i++) {
                    buttons.add((RadioButton) h.rb_g.getChildAt(i));
                }
            }
        } else if (position == activity.app.getSize()) {
            NewHolder holder1 = (NewHolder) holder;
            holder1.textView.setText(activity.resources.getString(R.string.New_recommendation));
        } else {
            GameShowHolder h = (GameShowHolder) holder;
            //imageViewSet.add(h.game_pic);
            AppModel appModel = labels.get(position - 1);
            ImageLoader.getInstance().displayImage(String.valueOf(appModel.getaLogo()), h.game_pic);
            ViewUtil.setContent(h.gamehow_txt, String.valueOf(appModel.getaSize()));
            ViewUtil.setContent(h.gamename_txt, String.valueOf(appModel.getaTitle()));
            h.game_doawload_btn.setBackgroundResource(R.drawable.round_bule_white_bg);
            h.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._03a9f3));
            String str = null;
            //下载按钮状态
            switch (appModel.getDownloadStatus()) {
                //下载完成
                case AppModel.DownloadStatus.DOWNLOAD_COMPLETE:
                    h.game_doawload_btn.setBackgroundResource(R.drawable.round_green_white_bg);
                    h.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._01ed44));
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
                    h.game_doawload_btn.setBackgroundResource(R.drawable.round_gray_bg);
                    h.game_doawload_btn.setTextColor(activity.resources.getColor(R.color._b6b6b6));
                    str = activity.resources.getString(R.string.download_install);
                    break;
                //升级
                case AppModel.DownloadStatus.UPGRADE:
                    str = activity.resources.getString(R.string.update);
                    break;
            }
            h.game_doawload_btn.setText(str);
            h.game_doawload_btn.setTag(R.id.bundle_params, position - 1);
            Map<String, Object> map = new HashMap<>();
            map.put("a", h.game_doawload_btn);
            h.game_doawload_btn.setTag(map);
            h.game_doawload_btn.setOnClickListener(this);
            h.game_pic.setTag(R.id.bundle_params, position - 1);
            h.game_pic.setOnClickListener(this);
            AppModelDownloadPool.attachViewToDownload(appModel, h.game_doawload_btn, null);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == activity.app.getSize() && position != 0 && cate.getcId() == 1000)
            return ITEM_VIEW_TYPE_BOTTOM;
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    @Override
    public void onClick(View v) {
        int position = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
        AppModel appModel = labels.get(position);
        switch (v.getId()) {
            case R.id.download_btn:
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

                break;
            case R.id.game_pic:
                Bundle bundle = new Bundle();
                bundle.putSerializable(IConstant.BUNDLE_PARAMS, appModel);
                Intent intent = new Intent(activity, GameDetailActivity.class);
                intent.putExtras(bundle);
                activity.startActivity(intent);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        buttons.get(position).setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public static class GameShowHolder extends RecyclerView.ViewHolder {
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

        public GameShowHolder(View itemView) {
            super(itemView);
            game_pic = ViewUtil.findViewById(itemView, R.id.game_pic);
            gamehow_txt = ViewUtil.findViewById(itemView, R.id.game_how_txt);
            game_doawload_btn = ViewUtil.findViewById(itemView, R.id.download_btn);
            gamename_txt = ViewUtil.findViewById(itemView, R.id.game_name_txt);
        }
    }

    /**
     * 广告Holder
     */
    public static class AdvHolder extends RecyclerView.ViewHolder {
        ViewPager viewPager;
        TextView gameTitle;
        RadioGroup rb_g;

        public AdvHolder(View itemView) {
            super(itemView);
            viewPager = ViewUtil.findViewById(itemView, R.id.frame_game_adv);
            gameTitle = ViewUtil.findViewById(itemView, R.id.game_title);
            rb_g = ViewUtil.findViewById(itemView, R.id.rb_g);
        }
    }

    public static class NewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public NewHolder(View itemView) {
            super(itemView);
            textView = ViewUtil.findViewById(itemView, R.id.bottom);
        }
    }

   /* public void recyCleView() {
        for (ImageView iv : imageViewSet) {
            if (iv != null) {
                try {
                    Drawable drawable = iv.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        Bitmap bitmap = bitmapDrawable.getBitmap();
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }*/
}
