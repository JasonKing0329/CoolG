package com.king.app.coolg.phone.record.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordListPhoneBinding;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.scene.SceneActivity;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;

import java.util.List;

/**
 * Created by Administrator on 2018/8/11 0011.
 */
@Route("RecordListPhone")
public class RecordPhoneListActivity extends MvvmActivity<ActivityRecordListPhoneBinding, RecordPhoneViewModel> implements IRecordListHolder {

    public static final int REQUEST_SCENE = 501;

    private RecordListFilterBean mFilter;

    private RecordListPagerAdapter pagerAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_list_phone;
    }

    @Override
    protected void initView() {
        initActionbar();
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

    private void initActionbar() {
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnSearchListener(words -> pagerAdapter.onKeywordChanged(words));
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_sort:
                    changeSortType();
                    break;
                case R.id.menu_filter:
                    changeFilter();
                    break;
                case R.id.menu_play:
                    pagerAdapter.showCanPlayList(true);
                    break;
                case R.id.menu_scene:
                    selectScene();
                    break;
            }
        });
    }

    private RecordListFragment getCurrentPage() {
        return pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
    }

    @Override
    protected RecordPhoneViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordPhoneViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.titlesObserver.observe(this, list -> showTitles(list));
        mModel.loadTitles();
    }

    private void showTitles(List<Integer> list) {
        String[] titles = AppConstants.RECORD_LIST_TITLES;
        if (pagerAdapter == null) {
            pagerAdapter = new RecordListPagerAdapter(getSupportFragmentManager());
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

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordOrderModeDesc());
        content.setSortType(SettingProperty.getRecordOrderMode());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setRecordOrderMode(sortMode);
            SettingProperty.setRecordOrderModeDesc(desc);
            pagerAdapter.onSortChanged();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        FilterDialogContent content = new FilterDialogContent();
        content.setFilterBean(mFilter);
        content.setOnFilterListener(bean -> {
            mFilter = bean;
            pagerAdapter.onFilterChanged(mFilter);
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    @Override
    public void updateCount(int recordType, int count) {
        String[] titles = AppConstants.RECORD_LIST_TITLES;
        mBinding.tabLayout.getTabAt(recordType).setText(titles[recordType] + "\n(" + count + ")");
    }

    private void selectScene() {
        Router.build("ScenePhone")
                .with(SceneActivity.EXTRA_SCENE, mModel.getScene())
                .requestCode(REQUEST_SCENE)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCENE) {
            if (resultCode == RESULT_OK) {
                mModel.setScene(data.getStringExtra(SceneActivity.RESP_SCENE));
                pagerAdapter.onSceneChanged(mModel.getScene());
            }
        }
    }
}
