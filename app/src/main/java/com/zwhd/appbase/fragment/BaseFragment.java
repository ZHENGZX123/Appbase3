package com.zwhd.appbase.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.http.BaseHttpHandler;
import com.zwhd.appbase.http.HttpHandler;
import com.zwhd.appbase.http.HttpResponseModel;

/**
 * 基础帧
 * <p/>
 * Created by YI on 2015/12/3.
 */
public class BaseFragment extends Fragment implements View.OnClickListener,HttpHandler{
    public BaseActivity activity;
    public View view;
    public BaseHttpHandler fragmenthttpHandler = new BaseHttpHandler(this) {
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (BaseActivity) getActivity();
    }

    @Override
    public void onClick(View v) {

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
