package com.king.app.coolg.phone.record.list;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.phone.video.home.RecommendBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/13 17:21
 */
public class RecordListPagerAdapter extends FragmentStatePagerAdapter {

    private List<RecordListFragment> fragmentList;
    private List<String> titleList;

    public RecordListPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        titleList = new ArrayList<>();
    }

    public void addFragment(RecordListFragment fragment, String title) {
        fragmentList.add(fragment);
        titleList.add(title);
    }

    @Override
    public RecordListFragment getItem(int position) {
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

    public void onSortChanged() {
        if (fragmentList != null) {
            for (RecordListFragment ft:fragmentList) {
                if (ft != null) {
                    ft.onSortChanged();
                }
            }
        }
    }

    public void onFilterChanged(RecommendBean filter) {
        if (fragmentList != null) {
            for (RecordListFragment ft:fragmentList) {
                if (ft != null) {
                    ft.onFilterChanged(filter);
                }
            }
        }
    }

    public void onSceneChanged(String scene) {
        if (fragmentList != null) {
            for (RecordListFragment ft:fragmentList) {
                if (ft != null) {
                    ft.onSceneChanged(scene);
                }
            }
        }
    }

    public void onKeywordChanged(String keyword) {
        if (fragmentList != null) {
            for (RecordListFragment ft:fragmentList) {
                if (ft != null) {
                    ft.onKeywordChanged(keyword);
                }
            }
        }
    }
}
