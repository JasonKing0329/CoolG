package com.king.app.coolg.phone.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityHomeBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
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

    private final int REQUEST_VIDEO_ORDER = 101;

    private ImageView navHeaderView;
    private ImageView ivFolder;
    private ImageView ivFace;

    private HomeAdapter adapter;
    private RecommendAdapter recommendAdapter;

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

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_recommend_setting:
                    showRecommendSetting();
                    break;
            }
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvItems.scrollToPosition(0));

        setBannerParams();
    }

    private void setBannerParams() {
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
            adapter.setOnListListener(new HomeAdapter.OnListListener() {
                @Override
                public void onLoadMore() {
                    mModel.loadMore();
                }

                @Override
                public void onClickItem(View view, Record record) {
                    goToRecord(record);
                }

                @Override
                public void onAddPlay(Record record) {
                    mModel.saveRecordToAddViewOrder(record);
                    Router.build("PlayOrder")
                            .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                            .requestCode(REQUEST_VIDEO_ORDER)
                            .go(HomeActivity.this);
                }
            });
            adapter.setOnHeadActionListener(new HomeAdapter.OnHeadActionListener() {
                @Override
                public void onClickStars() {
                    goToStarPage();
                }

                @Override
                public void onClickRecords() {
                    goToRecordPage();
                }

                @Override
                public void onClickOrders() {
                    goToOrderPage();
                }

                @Override
                public void onClickStudios() {
                    goToStudioPage();
                }

                @Override
                public void onClickPlayItem(View view, Record record) {
                    goToRecord(record);
                }

                @Override
                public void goToPlayList() {
                    goToPlayListPage();
                }
            });
            mBinding.rvItems.setAdapter(adapter);
        });
        mModel.newRecordsObserver.observe(this, number -> {
            int start = adapter.getItemCount() - number - 1;
            adapter.notifyItemRangeInserted(start, number);
        });
        mModel.recommendObserver.observe(this, record -> {
            recommendAdapter = new RecommendAdapter();
            recommendAdapter.setOnItemListener(new RecommendAdapter.OnItemListener() {
                @Override
                public Record getNewItem() {
                    return mModel.newRecommend();
                }

                @Override
                public void onClickItem(Record record) {
                    goToRecord(record);
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

    private void showRecommendSetting() {
        RecommendFilterFragment content = new RecommendFilterFragment();
        content.setOnRecordFilterListener(model -> {
            mModel.updateRecordFilter(model);
            setBannerParams();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight());
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.show(getSupportFragmentManager(), "RecommendFilterFragment");
    }

    private void goToRecord(Record record) {
        if (record == null) {
            showMessageShort("record is null");
            return;
        }
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, record.getId())
                .go(this);
    }

    private void goToStarPage() {
        Router.build("StarListPhone")
                .go(this);
    }

    private void goToRecordPage() {
        Router.build("RecordListPhone")
                .go(this);
    }

    private void goToOrderPage() {
        Router.build("OrderPhone")
                .go(this);
    }

    private void goToStudioPage() {
        Router.build("StudioPhone")
                .go(this);
    }

    private void goToPlayListPage() {
        Router.build("VideoHomePhone")
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_VIDEO_ORDER:
                if (resultCode == RESULT_OK) {
                    ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                    mModel.insertToPlayList(list);
                }
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.stopImageTimerTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.startImageTimerTask();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.clearImageTimerTask();
        }
    }

}
