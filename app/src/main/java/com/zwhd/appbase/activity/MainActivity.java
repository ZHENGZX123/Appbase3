package com.zwhd.appbase.activity;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.zwhd.advsdk.GameFlyAd;
import com.zwhd.appbase.IConstant;
import com.zwhd.appbase.IUrconstant;
import com.zwhd.appbase.R;
import com.zwhd.appbase.adapter.ViewPagerAdapter;
import com.zwhd.appbase.download.AppModelDownloadPool;
import com.zwhd.appbase.download.AppModelUtil;
import com.zwhd.appbase.download.CateModel;
import com.zwhd.appbase.fragment.FirstFragment;
import com.zwhd.appbase.http.HttpResponseModel;
import com.zwhd.appbase.util.StringUtil;
import com.zwhd.appbase.util.ViewUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends BaseActivity implements View.OnKeyListener {
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    EditText editText;
    AppBarLayout appBar;
    FirstFragment firstFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appBar=ViewUtil.findViewById(this,R.id.appbar);
        editText = ViewUtil.findViewById(this, R.id.search_game_edit);
        editText.setOnKeyListener(this);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        firstFragment = new FirstFragment();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), new ArrayList<CateModel>());
        viewPager.setAdapter(adapter);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        findViewById(R.id.search_button).setOnClickListener(this);
        findViewById(R.id.downloads).setOnClickListener(this);
        IConstant.HTTP_CONNECT_POOL.addRequest(IUrconstant.CATE_GORY_URL, null, httpHandler);
        GameFlyAd.onCreate(this, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppModelDownloadPool.updateAllStatus(getContentResolver());
        AppModelUtil.updateInstallStatus(getContentResolver());
        int dcount = AppModelUtil.loadDownloadCount(getContentResolver(),true);
        if (dcount > 0) {
            ViewUtil.setContent(this, R.id.game_dawnload, String.valueOf(dcount));
            findViewById(R.id.downloads_img).setVisibility(View.GONE);
            findViewById(R.id.game_dawnload).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.downloads_img).setVisibility(View.VISIBLE);
            findViewById(R.id.game_dawnload).setVisibility(View.GONE);
        }
        dcount=AppModelUtil.loadDownloadCount(getContentResolver(),false);
        if(dcount>0){
            findViewById(R.id.item_progress).setVisibility(View.VISIBLE);
            findViewById(R.id.game_dawnload).setBackgroundResource(R.color.transparent);
        }else{
            findViewById(R.id.item_progress).setVisibility(View.GONE);
            findViewById(R.id.game_dawnload).setBackgroundResource(R.drawable.round_white_shape);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /***
             * 搜索按钮
             * */
            case R.id.search_button:
                adapter.fragments[viewPager.getCurrentItem()].search();
                ViewUtil.hideKeyboard(this);
                break;
            /**
             * 下载详情
             * */
            case R.id.downloads:
                Intent intent = new Intent(this, GameListActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    public void httpSuccess(HttpResponseModel message) throws Exception {
        super.httpSuccess(message);
        CateModel cate1 = new CateModel();
        cate1.setcId(1000);
        cate1.setcName("Hot");
        adapter.cates.add(cate1);
        JSONObject object = new JSONObject(new String(message.getResponse()));
        JSONArray data = null;
        data = object.optJSONArray("data");
        if (data != null) {
            for (int i = 0, leg = data.length(); i < leg; i++) {
                JSONObject item = data.getJSONObject(i);
                CateModel cate = new CateModel();
                String name = null;
                Integer id = null;
                name = item.optString("name");
                id = item.optInt("id");
                cate.setcName(name);
                cate.setcId(id);
                if (StringUtil.isNotEmpty(name) && id != null) {
                    adapter.cates.add(cate);
                }
            }
            adapter.notifyDataSetChanged();
            tabLayout.setupWithViewPager(viewPager);
        }
    }

    void Animation() throws Exception {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 200, Animation.RELATIVE_TO_PARENT, 100,
                Animation.RELATIVE_TO_PARENT, 200, Animation.RELATIVE_TO_PARENT, 100
        );
        translateAnimation.setDuration(1000);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0f);
        scaleAnimation.setDuration(1000);
        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(scaleAnimation);
        findViewById(R.id.game_dawnload).startAnimation(animationSet);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppModelDownloadPool.pauseAll();
        GameFlyAd.onDestory(false);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            ViewUtil.hideKeyboard(this);
            adapter.fragments[viewPager.getCurrentItem()].search();
            return true;
        }
        return false;
    }
}
