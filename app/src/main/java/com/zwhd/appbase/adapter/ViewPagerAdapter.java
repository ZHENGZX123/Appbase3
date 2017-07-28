package com.zwhd.appbase.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.zwhd.appbase.download.CateModel;
import com.zwhd.appbase.fragment.GamePageFragment;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by zaoxin on 2015/12/3.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public List<CateModel> cates;
    public GamePageFragment[] fragments;

    public ViewPagerAdapter(FragmentManager fm, List<CateModel> cates) {
        super(fm);
        this.cates = cates;
    }

    @Override
    public void notifyDataSetChanged() {
        fragments = new GamePageFragment[cates.size()];
        super.notifyDataSetChanged();
    }

    @Override
    public GamePageFragment getItem(int position) {
        if (fragments[position] == null)
            fragments[position] = GamePageFragment.newInstance(cates.get(position));
        return fragments[position];
    }

    @Override
    public int getCount() {
        return cates == null ? 0 : cates.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return cates.get(position).getcName();
    }
}
