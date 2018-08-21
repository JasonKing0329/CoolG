package com.king.app.coolg.pad.star.list;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.PopupMenu;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarListPadBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.pad.record.list.IRecordListHolder;
import com.king.app.coolg.pad.record.list.RecordListPadFragment;
import com.king.app.coolg.pad.star.StarPadActivity;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.list.IStarListHolder;
import com.king.app.coolg.phone.star.list.StarListFragment;
import com.king.app.coolg.phone.star.list.StarListPagerAdapter;
import com.king.app.coolg.phone.star.list.StarListTitleViewModel;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/20 11:09
 */
@Route("StarListPad")
public class StarListPadActivity extends MvvmActivity<ActivityStarListPadBinding, StarListTitleViewModel>
    implements IStarListHolder, IRecordListHolder {

    private StarListPagerAdapter pagerAdapter;

    private String curDetailIndex;

    private RecordListPadFragment ftRecord;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_list_pad;
    }

    @Override
    protected void initView() {
        initActionbar();
        mBinding.fabTop.setOnClickListener(view -> getCurrentPage().goTop());
        // 默认只缓存另外2个，在切换时处理view mode及sort type有很多弊端，改成缓存全部另外3个可以规避问题
        mBinding.viewpager.setOffscreenPageLimit(3);
        mBinding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagerAdapter.getItem(position).onRefresh(mModel.getSortMode());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mBinding.ivIconSort.setOnClickListener(v -> changeSortType());
    }

    @Override
    protected StarListTitleViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarListTitleViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.menuViewModeObserver.observe(this, title -> mBinding.actionbar.updateMenuText(R.id.menu_gdb_view_mode, title));
        mModel.sortTypeObserver.observe(this, type -> getCurrentPage().updateSortType(type));
        mModel.titlesObserver.observe(this, list -> showTitles(list));
        mModel.loadTitles();
    }

    private void initActionbar() {
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnSearchListener(words -> getCurrentPage().filterStar(words));
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            if (iconMenuId == R.id.menu_sort) {
                return createSortPopup(anchorView);
            }
            return null;
        });
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_index:
                    changeSideBarVisible();
                    break;
                case R.id.menu_gdb_view_mode:
                    mModel.toggleViewMode(getResources());
                    pagerAdapter.onViewModeChanged();
                    break;
                case R.id.menu_gdb_expand_all:
                    getCurrentPage().setExpandAll(true);
                    break;
                case R.id.menu_gdb_collapse_all:
                    getCurrentPage().setExpandAll(false);
                    break;
            }
        });
    }

    private PopupMenu createSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(this, anchorView);
        menu.getMenuInflater().inflate(R.menu.player_sort, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_name:
                    mModel.setSortMode(AppConstants.STAR_SORT_NAME);
                    break;
                case R.id.menu_sort_records:
                    mModel.setSortMode(AppConstants.STAR_SORT_RECORDS);
                    break;
                case R.id.menu_sort_rating:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING);
                    break;
                case R.id.menu_sort_rating_face:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_FACE);
                    break;
                case R.id.menu_sort_rating_body:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_BODY);
                    break;
                case R.id.menu_sort_rating_dk:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_DK);
                    break;
                case R.id.menu_sort_rating_sexuality:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_SEXUALITY);
                    break;
                case R.id.menu_sort_rating_passion:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_PASSION);
                    break;
                case R.id.menu_sort_rating_video:
                    mModel.setSortMode(AppConstants.STAR_SORT_RATING_VIDEO);
                    break;
            }
            return false;
        });
        return menu;
    }

    public void changeSideBarVisible() {
        getCurrentPage().toggleSidebar();
    }

    private StarListFragment getCurrentPage() {
        return pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
    }

    private void showTitles(List<Integer> list) {
        String[] titles = AppConstants.STAR_LIST_TITLES;
        if (pagerAdapter == null) {
            pagerAdapter = new StarListPagerAdapter(getSupportFragmentManager());
            StarListFragment fragmentAll = StarListFragment.newInstance(DataConstants.STAR_MODE_ALL);
            pagerAdapter.addFragment(fragmentAll, titles[0] + "(" + list.get(0) + ")");
            StarListFragment fragment1 = StarListFragment.newInstance(DataConstants.STAR_MODE_TOP);
            pagerAdapter.addFragment(fragment1, titles[1] + "(" + list.get(1) + ")");
            StarListFragment fragment0 = StarListFragment.newInstance(DataConstants.STAR_MODE_BOTTOM);
            pagerAdapter.addFragment(fragment0, titles[2] + "(" + list.get(2) + ")");
            StarListFragment fragment05 = StarListFragment.newInstance(DataConstants.STAR_MODE_HALF);
            pagerAdapter.addFragment(fragment05, titles[3] + "(" + list.get(3) + ")");
            mBinding.viewpager.setAdapter(pagerAdapter);
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[0]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[1]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[2]));
            mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[3]));
            mBinding.tabLayout.setupWithViewPager(mBinding.viewpager);
        }
        else {
            mBinding.tabLayout.removeAllTabs();
            for (int i = 0; i < titles.length; i ++) {
                mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[i] + "(" + list.get(i) + ")"));
            }
        }
    }

    @Override
    public boolean dispatchClickStar(Star star) {
        if (ftRecord == null) {
            ftRecord = RecordListPadFragment.newInstance(star.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.group_ft_record, ftRecord, "RecordListPadFragment")
                    .commit();
        }
        else {
            ftRecord.onStarChanged(star.getId());
        }
        return true;
    }

    @Override
    public boolean dispatchOnLongClickStar(Star star) {
        Router.build("StarPad")
                .with(StarPadActivity.EXTRA_STAR_ID, star.getId())
                .go(this);
        return true;
    }

    @Override
    public void hideDetailIndex() {
        mBinding.tvIndex.setVisibility(View.GONE);
    }

    @Override
    public void updateDetailIndex(String name) {
        if (mBinding.tvIndex.getVisibility() != View.VISIBLE) {
            mBinding.tvIndex.setVisibility(View.VISIBLE);
        }

        String newIndex = getAvailableIndex(name);
        if (!newIndex.equals(curDetailIndex)) {
            curDetailIndex = newIndex;
            mBinding.tvIndex.setText(newIndex);
        }
    }

    /**
     * 最多支持3个字母
     * @param name
     * @return
     */
    private String getAvailableIndex(String name) {
        if (name.length() > 2) {
            return name.substring(0,3);
        }
        else if (name.length() > 1) {
            return name.substring(0,2);
        }
        else if (name.length() > 0) {
            return name.substring(0,1);
        }
        return "";
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordSortDesc());
        content.setSortType(SettingProperty.getRecordSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setStarRecordsSortType(sortMode);
            SettingProperty.setStarRecordsSortDesc(desc);
            ftRecord.onStarRecordsSortChanged();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    @Override
    public void showRecordPopup(View v, Record record) {

    }
}
