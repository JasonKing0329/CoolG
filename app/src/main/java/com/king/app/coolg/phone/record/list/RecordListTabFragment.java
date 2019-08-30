package com.king.app.coolg.phone.record.list;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.FragmentRecordListTabPhoneBinding;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.view.dialog.SimpleDialogs;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/8/30 14:10
 */
public class RecordListTabFragment extends MvvmFragment<FragmentRecordListTabPhoneBinding, RecordPhoneViewModel> implements IRecordListHolder {

    private RecordListPagerAdapter pagerAdapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_record_list_tab_phone;
    }

    @Override
    protected RecordPhoneViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordPhoneViewModel.class);
    }

    @Override
    protected void onCreate(View view) {

        // 默认只缓存另外2个，在切换时处理view mode及sort type有很多弊端，改成缓存全部另外4个可以规避问题
        mBinding.viewpager.setOffscreenPageLimit(4);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onCreateData() {
        mModel.titlesObserver.observe(this, list -> showTitles(list));
        mModel.loadTitles();
    }

    private RecordListFragment getCurrentPage() {
        return pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
    }

    public void onKeywordChanged(String words) {
        pagerAdapter.onKeywordChanged(words);
    }

    public void showSetOffset() {
        new SimpleDialogs().openInputDialog(getContext(), "set offset", name -> {
            try {
                getCurrentPage().setOffset(Integer.parseInt(name));
            } catch (Exception e) {}
        });
    }

    private void showTitles(List<Integer> list) {
        String[] titles = AppConstants.RECORD_LIST_TITLES;
        if (pagerAdapter == null) {
            pagerAdapter = new RecordListPagerAdapter(getChildFragmentManager());
            for (int i = 0; i < titles.length; i ++) {
                RecordListFragment fragmentAll = RecordListFragment.newInstance(i, mModel.getScene());
                pagerAdapter.addFragment(fragmentAll, titles[i] + "\n(" + list.get(i) + ")");
                mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[i]));
            }
            mBinding.viewpager.setAdapter(pagerAdapter);
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[0]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[1]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[2]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[3]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[4]));
            mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
        }
        else {
            mBinding.tabLayout.removeAllTabs();
            for (int i = 0; i < titles.length; i ++) {
                mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[i] + "\n(" + list.get(i) + ")"));
            }
        }
    }

    public void onSortChanged() {
        pagerAdapter.onSortChanged();
    }

    public Integer getCurrentItem() {
        return mBinding.viewpager.getCurrentItem();
    }

    public void onFilterChanged(RecommendBean mFilter) {
        pagerAdapter.onFilterChanged(mFilter);
    }

    public void onSceneChanged(String scene) {
        mModel.setScene(scene);
        pagerAdapter.onSceneChanged(scene);
    }

    @Override
    public void updateCount(int recordType, int count) {
        String[] titles = AppConstants.RECORD_LIST_TITLES;
        mBinding.tabLayout.getTabAt(recordType).setText(titles[recordType] + "\n(" + count + ")");
    }

}
