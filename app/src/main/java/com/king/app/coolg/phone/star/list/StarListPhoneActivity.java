package com.king.app.coolg.phone.star.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityStarListPhoneBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
public class StarListPhoneActivity extends MvvmActivity<ActivityStarListPhoneBinding, StarPhoneViewModel>
    implements IStarListHolder {

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
        initBanner();
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
        content.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {

            @Override
            public void onRandomAnim(boolean random) {
                SettingProperty.setStarRandomRecommend(random);
            }

            @Override
            public boolean isRandomAnim() {
                return SettingProperty.isStarRandomRecommend();
            }

            @Override
            public int getAnimType() {
                return SettingProperty.getRecommendAnimType();
            }

            @Override
            public void onSaveAnimType(int type) {
                SettingProperty.setRecommendAnimType(type);
            }

            @Override
            public int getAnimTime() {
                return SettingProperty.getRecommendAnimTime();
            }

            @Override
            public void onSaveAnimTime(int time) {
                SettingProperty.setRecommendAnimTime(time);
            }

            @Override
            public void onParamsSaved() {
                initBanner();
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

    private void initBanner() {
        // 禁用btnStart(只在onPageScroll触发后有效)
        mBinding.lmbanner.isGuide(false);
        // 不显示引导圆点
        mBinding.lmbanner.hideIndicatorLayout();
        // 轮播切换时间
        mBinding.lmbanner.setDurtion(SettingProperty.getStarRecommendAnimTime());
        if (SettingProperty.isStarRandomRecommend()) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(mBinding.lmbanner, type);
        }
        else {
            LMBannerViewUtil.setScrollAnim(mBinding.lmbanner, SettingProperty.getStarRecommendAnimType());
        }
    }

    private void initRecommend() {

        // 采用getView时生成随机推荐，这里初始化3个item就够了（LMBanner内部也是根据view pager设置下标
        // 来循环的）
        List<Star> list = new ArrayList<>();
        list.add(new Star());
        list.add(new Star());
        list.add(new Star());
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        mBinding.lmbanner.setAdapter(adapter, list);

        mBinding.progress.setVisibility(View.GONE);
        // 这里一定要加载完后再设置可见，因为LMBanners的内部代码里有一个btnStart，本来是受isGuide的控制
        // 但是1.0.8版本里只在onPageScroll里面判断了这个属性。导致如果一开始LMBanners处于可见状态，
        // adapter里还没有数据，btnStart就会一直显示在那里，直到开始触发onPageScroll才会隐藏
        // 本来引入library，在setGuide把btnStart的visibility置为gone就可以了，但是这个项目已经引入了很多module了，就不再引入了
        mBinding.lmbanner.setVisibility(View.VISIBLE);
    }

    @Override
    protected StarPhoneViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarPhoneViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.menuViewModeObserver.observe(this, title -> mBinding.actionbar.updateMenuText(R.id.menu_gdb_view_mode, title));
        mModel.sortTypeObserver.observe(this, type -> getCurrentPage().updateSortType(type));
        mModel.titlesObserver.observe(this, list -> showTitles(list));
        mModel.loadTitles();
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

    private StarListFragment getCurrentPage() {
        return pagerAdapter.getItem(mBinding.viewpager.getCurrentItem());
    }

    @Override
    public boolean dispatchClickStar(Star star) {
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
        if (mBinding.lmbanner != null) {
            mBinding.lmbanner.stopImageTimerTask();
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
        if (mBinding.lmbanner != null) {
            mBinding.lmbanner.startImageTimerTask();
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
        if (mBinding.lmbanner != null) {
            mBinding.lmbanner.clearImageTimerTask();
        }
    }

    private class HeadBannerAdapter implements LBaseAdapter<Star>, View.OnClickListener {

        private RequestOptions requestOptions;

        public HeadBannerAdapter() {
            requestOptions = GlideUtil.getRecordOptions();
        }

        @Override
        public View getView(LMBanners lBanners, Context context, int position, Star bean) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_image, null);

            bean = mModel.nextFavorStar();
            if (bean != null) {
                ImageView imageView = view.findViewById(R.id.iv_image);
                String path = ImageProvider.getStarRandomPath(bean.getName(), null);

                Glide.with(context)
                        .load(path)
                        .apply(requestOptions)
                        .into(imageView);

                RelativeLayout groupContainer = view.findViewById(R.id.group_container);
                groupContainer.setTag(bean);
                groupContainer.setOnClickListener(this);
            }
            return view;
        }

        @Override
        public void onClick(View v) {
            Star bean = (Star) v.getTag();
            onClickBannerItem(bean);
        }

    }
    
    private void onClickBannerItem(Star bean) {
//        ActivityManager.startStarActivity(this, bean.getId());
    }

}
