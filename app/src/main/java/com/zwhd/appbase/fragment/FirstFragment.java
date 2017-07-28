package com.zwhd.appbase.fragment;

import android.os.Bundle;

import com.zwhd.appbase.download.AdvModel;

/**
 * Created by zaoxin on 2016/1/28.
 */
public class FirstFragment extends BaseFragment {

    private static final String ARG_PARAM1 = "ffff";

    public static FirstFragment newInstance() {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
}
