package com.zwhd.appbase.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zwhd.appbase.R;
import com.zwhd.appbase.download.AdvModel;
import com.zwhd.appbase.util.ViewUtil;

public class AdvFragment extends BaseFragment {
    private static final String ARG_PARAM1 = "param1";
    AdvModel advModel;

    public AdvFragment() {

    }

    public static AdvFragment newInstance(AdvModel advModel) {
        AdvFragment fragment = new AdvFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            advModel = (AdvModel) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return ViewUtil.inflate(activity,R.layout.fragment_adv);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView iv=ViewUtil.findViewById(view,R.id.adv_img);
        ImageLoader.getInstance().displayImage(advModel.getResourceUrl(), iv);
    }
}
