package com.king.app.coolg.phone.home;

import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityHomeBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 15:14
 */
@Route("Home")
public class HomeActivity extends MvvmActivity<ActivityHomeBinding, HomeViewModel>
    implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private HomeAdapter adapter;
    private RecordRecommendAdapter recommendAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        mBinding.navView.setNavigationItemSelectedListener(this);
        mBinding.navView.setItemIconTintList(null);
        navHeaderView = mBinding.navView.getHeaderView(0).findViewById(R.id.nav_header_bg);
        ivFolder = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_folder);
        ivFace = mBinding.navView.getHeaderView(0).findViewById(R.id.iv_face);

        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvItems.setEnableLoadMore(true);
        mBinding.rvItems.setOnLoadMoreListener(() -> mModel.loadMore());

        initBanner();
    }

    private void initBanner() {
        // 禁用btnStart(只在onPageScroll触发后有效)
        mBinding.banner.isGuide(false);
        // 不显示引导圆点
        mBinding.banner.hideIndicatorLayout();
        int time = SettingProperty.getRecommendAnimTime();
        if (time < 3000) {
            time = 3000;
        }
        // 轮播切换时间
        mBinding.banner.setDurtion(time);

//        mLBanners.setAutoPlay(true);//自动播放
//        mLBanners.setVertical(false);//是否锤子播放
//        mLBanners.setScrollDurtion(2000);//两页切换时间
//        mLBanners.setCanLoop(true);//循环播放
//        mLBanners.setSelectIndicatorRes(R.drawable.guide_indicator_select);//选中的原点
//        mLBanners.setUnSelectUnIndicatorRes(R.drawable.guide_indicator_unselect);//未选中的原点
//        //若自定义原点到底部的距离,默认20,必须在setIndicatorWidth之前调用
//        mLBanners.setIndicatorBottomPadding(30);
//        mLBanners.setIndicatorWidth(10);//原点默认为5dp
//        mLBanners.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);//设置原点显示位置

        if (SettingProperty.isRandomRecommend()) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(mBinding.banner, type);
        }
        else {
            LMBannerViewUtil.setScrollAnim(mBinding.banner, SettingProperty.getRecommendAnimType());
        }
    }

    @Override
    protected HomeViewModel createViewModel() {
        return ViewModelProviders.of(this).get(HomeViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.homeObserver.observe(this, bean -> {
            adapter = new HomeAdapter();
            adapter.setList(bean.getRecordList());
            mBinding.rvItems.setAdapter(adapter);
        });
        mModel.newRecordsObserver.observe(this, number -> {
            int start = adapter.getItemCount() - number - 1;
            adapter.notifyItemRangeInserted(start, number);
        });
        mModel.recommendObserver.observe(this, record -> {
            recommendAdapter = new RecordRecommendAdapter();
            recommendAdapter.setOnItemListener(new RecordRecommendAdapter.OnItemListener() {
                @Override
                public Record getNewItem() {
                    return mModel.newRecommend();
                }

                @Override
                public void onClickItem(Record record) {

                }
            });
            List<Record> list = new ArrayList<>();
            list.add(record);
            list.add(record);
            list.add(record);
            mBinding.banner.setAdapter(recommendAdapter, list);
            mBinding.ivRecord.setVisibility(View.GONE);
            mBinding.banner.setVisibility(View.VISIBLE);
        });

        mModel.loadData();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

}
