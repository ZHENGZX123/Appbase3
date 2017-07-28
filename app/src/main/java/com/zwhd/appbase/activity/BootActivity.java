package com.zwhd.appbase.activity;

import android.os.Bundle;

import com.zwhd.appbase.R;
import com.zwhd.appbase.http.HttpResponseModel;

/**
 * Created by zaoxin on 2015/12/9.
 */
public class BootActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.item_adv_img);

    }

    @Override
    public void success(HttpResponseModel message) throws Exception {
        super.success(message);

    }
}
