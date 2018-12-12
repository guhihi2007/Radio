package cn.yuntk.radio.ibook.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import java.util.List;

import cn.yuntk.radio.ibook.base.BaseFragment;

/*历史 fragment适配器*/
public class ViewPagerAdapter<T extends BaseFragment> extends FragmentPagerAdapter {

    List<T> fragments;

    public ViewPagerAdapter(FragmentManager fm, List<T> fragments) {
        super(fm);
        this.fragments = fragments;
    }


    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null?0:fragments.size();
    }
}
