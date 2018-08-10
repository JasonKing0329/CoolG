package com.king.app.coolg.phone.star.list;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/13 17:21
 */
public class StarListPagerAdapter extends FragmentStatePagerAdapter {

    private List<StarListFragment> fragmentList;
    private List<String> titleList;

    public StarListPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    public void addFragment(StarListFragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    public void updateTitle(int index, String title) {
        titleList.set(index, title);
    }

    @Override
    public StarListFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }

    public void onViewModeChanged() {
        if (fragmentList != null) {
            for (StarListFragment ft:fragmentList) {
                if (ft != null) {
                    ft.onViewModeChanged();
                }
            }
        }
    }
}
