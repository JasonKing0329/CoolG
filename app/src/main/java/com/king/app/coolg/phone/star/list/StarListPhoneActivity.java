package com.king.app.coolg.phone.star.list;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarListPhoneBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;
import com.king.lib.banner.CoolBannerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 17:04
 */
@Route("StarListPhone")
public class StarListPhoneActivity extends MvvmActivity<ActivityStarListPhoneBinding, StarListTitleViewModel>
    implements IStarListHolder {

    public static final String EXTRA_STUDIO_ID = "studio_id";

    private StarListPagerAdapter pagerAdapter;

    /**
     * 控制detail index显示的timer
     */
    private Disposable indexDisposable;
    private String curDetailIndex;

    @Override
    protected int getContentView() {
        return R.layout.activity_star_list_phone;
    }

    @Override
    protected void initView() {
        initActionbar();
        BannerHelper.setBannerParams(mBinding.banner, ViewProperty.getStarBannerParams());
        initRecommend();
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
        
        mBinding.groupSetting.setOnClickListener(v -> showSettings());
    }

    private void showSettings() {
        BannerSettingFragment content = new BannerSettingFragment();
        content.setParams(ViewProperty.getRecordBannerParams());
        content.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onParamsUpdated(BannerParams params) {

            }

            @Override
            public void onParamsSaved(BannerParams params) {
                ViewProperty.setRecordBannerParams(params);
                BannerHelper.setBannerParams(mBinding.banner, params);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Banner Setting");
        dialogFragment.show(getSupportFragmentManager(), "BannerSettingFragment");
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
                case R.id.menu_gdb_category:
                    goToCategory();
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

    private void goToCategory() {
        Router.build("Category")
                .go(this);
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
                case R.id.menu_sort_random:
                    mModel.setSortMode(AppConstants.STAR_SORT_RANDOM);
                    break;
            }
            return false;
        });
        return menu;
    }

    private void changeSideBarVisible() {
        getCurrentPage().toggleSidebar();
    }

    private void initRecommend() {

        // 采用getView时生成随机推荐，这里初始化5个（引发pageradapter的重新创建view操作）
        List<Star> list = new ArrayList<>();
        list.add(new Star());
        list.add(new Star());
        list.add(new Star());
        list.add(new Star());
        list.add(new Star());
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        adapter.setList(list);
        mBinding.banner.setAdapter(adapter);
        mBinding.banner.startAutoPlay();
    }

    @Override
    protected StarListTitleViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarListTitleViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.menuViewModeObserver.observe(this, title -> mBinding.actionbar.updateMenuText(R.id.menu_gdb_view_mode, title));
        mModel.sortTypeObserver.observe(this, type -> getCurrentPage().updateSortType(type));
//        mModel.titlesObserver.observe(this, list -> showTitles(list));
//        mModel.loadTitles();
        showTitles();

        if (getStudioId() != 0) {
            String studio = mModel.getStudioName(getStudioId());
            mBinding.actionbar.setTitle(studio);
        }
    }

    private long getStudioId() {
        return getIntent().getLongExtra(EXTRA_STUDIO_ID, 0);
    }

    @Override
    public void updateTabTitle(String starType, String title) {
        if (starType.equals(DataConstants.STAR_MODE_ALL)) {
            pagerAdapter.updateTitle(0, title);
            mBinding.tabLayout.getTabAt(0).setText(title);
        }
        else if (starType.equals(DataConstants.STAR_MODE_TOP)) {
            pagerAdapter.updateTitle(1, title);
            mBinding.tabLayout.getTabAt(1).setText(title);
        }
        else if (starType.equals(DataConstants.STAR_MODE_BOTTOM)) {
            pagerAdapter.updateTitle(2, title);
            mBinding.tabLayout.getTabAt(2).setText(title);
        }
        else if (starType.equals(DataConstants.STAR_MODE_HALF)) {
            pagerAdapter.updateTitle(3, title);
            mBinding.tabLayout.getTabAt(3).setText(title);
        }
    }

    private void showTitles() {
        String[] titles = AppConstants.STAR_LIST_TITLES;
        if (pagerAdapter == null) {
            pagerAdapter = new StarListPagerAdapter(getSupportFragmentManager());
            StarListFragment fragmentAll = StarListFragment.newInstance(DataConstants.STAR_MODE_ALL, getStudioId());
            pagerAdapter.addFragment(fragmentAll, titles[0]);
            StarListFragment fragment1 = StarListFragment.newInstance(DataConstants.STAR_MODE_TOP, getStudioId());
            pagerAdapter.addFragment(fragment1, titles[1]);
            StarListFragment fragment0 = StarListFragment.newInstance(DataConstants.STAR_MODE_BOTTOM, getStudioId());
            pagerAdapter.addFragment(fragment0, titles[2]);
            StarListFragment fragment05 = StarListFragment.newInstance(DataConstants.STAR_MODE_HALF, getStudioId());
            pagerAdapter.addFragment(fragment05, titles[3]);
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
                mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(titles[i]));
            }
        }
    }

    private StarListFragment getCurrentPage() {
        return pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
    }

    @Override
    public boolean dispatchClickStar(Star star) {
        return false;
    }

    @Override
    public boolean dispatchOnLongClickStar(Star star) {
        return false;
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

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding.banner != null) {
            mBinding.banner.stopAutoPlay();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (indexDisposable != null) {
            indexDisposable.dispose();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding.banner != null) {
            mBinding.banner.startAutoPlay();
        }

        // 控制tvIndex在切换显示列表后的隐藏状况
        indexDisposable = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mBinding.tvIndex != null && mBinding.tvIndex.getVisibility() == View.VISIBLE) {
                        if (mBinding.viewpager != null && pagerAdapter != null) {
                            if (getCurrentPage().isNotScrolling()) {
                                mBinding.tvIndex.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding.banner != null) {
            mBinding.banner.stopAutoPlay();
        }
    }

    private class HeadBannerAdapter extends CoolBannerAdapter<Star> implements View.OnClickListener {

        @Override
        protected int getLayoutRes() {
            return R.layout.adapter_banner_image;
        }

        @Override
        protected void onBindView(View view, int position, Star bean) {
            bean = mModel.nextFavorStar();
            if (bean != null) {
                ImageView imageView = view.findViewById(R.id.iv_image);
                String path = ImageProvider.getStarRandomPath(bean.getName(), null);

                GlideApp.with(view.getContext())
                        .load(path)
                        .error(R.drawable.def_person)
                        .into(imageView);

                ViewGroup groupContainer = view.findViewById(R.id.group_container);
                groupContainer.setTag(bean);
                groupContainer.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            Star bean = (Star) v.getTag();
            onClickBannerItem(bean);
        }

    }
    
    private void onClickBannerItem(Star bean) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, bean.getId())
                .go(this);
    }

}
