package com.zwhd.appbase.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zwhd.appbase.activity.BaseActivity;
import com.zwhd.appbase.download.AdvModel;
import com.zwhd.appbase.fragment.AdvFragment;

import java.util.List;

/**
 * Created by YI on 2015/12/12.
 */
public class AdvFragmentAdapter extends FragmentStatePagerAdapter{
    List<AdvModel> advModels;


    public AdvFragmentAdapter(FragmentManager fm, List<AdvModel> advModels) {
        super(fm);
       this.advModels=advModels;
    }

    @Override
    public Fragment getItem(int position) {
        return AdvFragment.newInstance(advModels.get(position));
    }

    @Override
    public int getCount() {
        return advModels.size();
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }
}
