package com.zwhd.appbase.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.activity.GameDetailActivity;
import com.zwhd.appbase.activity.MainActivity;
import com.zwhd.appbase.download.AdvModel;
import com.zwhd.appbase.download.AppConstant;
import com.zwhd.appbase.download.AppModel;
import com.zwhd.appbase.util.Logger;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaoxin on 2015/12/9.
 */
public class AdvAdapter extends PagerAdapter implements View.OnClickListener {
    BaseActivity activity;
    public List<AppModel> models;
    List<RadioButton> buttons = new ArrayList<>();
    public AdvAdapter(Context context, List<AppModel> models,List<RadioButton> buttons) {
        this.activity = (BaseActivity) context;
        this.models = models;
        this.buttons = buttons;
    }
    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        ImageView view = ViewUtil.inflate(AppConstant.app, R.layout.item_adv_img);
        AppModel model = models.get(position);
       ImageLoader.getInstance().displayImage(model.getAdvurl(), view);
        collection.addView(view);
        view.setTag(R.id.bundle_params,position);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
      /*  try {
            Drawable drawable = ((ImageView)view).getDrawable();
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                Bitmap bitmap = bitmapDrawable.getBitmap();
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getCount() {
        return models == null ? 0 : models.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void onClick(View v) {
        try {
            int position = StringUtil.toInt(v.getTag(R.id.bundle_params).toString());
            AppModel model = models.get(position);
            Intent intent = new Intent(activity, GameDetailActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(IConstant.BUNDLE_PARAMS,model);
            intent.putExtras(bundle);
            activity.startActivity(intent);
            buttons.get(0).setChecked(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
