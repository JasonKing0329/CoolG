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
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.app.gdb.data.entity.Record;
import com.king.lib.banner.BannerFlipStyleProvider;

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
                case R.id.menu_anim_setting:
                    showAnimSetting();
                    break;
            }
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvItems.scrollToPosition(0));
        BannerHelper.setBannerParams(mBinding.banner, ViewProperty.getHomeBannerParams());
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

            // RecommendAdapter采用通过回调每次加载新的record，因此在默认缓存3个item的条件下，将list固定为5个，以便保证引起pageradapter的重新创建view从而调用onbindview
            List<Record> list = new ArrayList<>();
            list.add(record);
            list.add(record);
            list.add(record);
            list.add(record);
            list.add(record);
            recommendAdapter.setList(list);

            mBinding.banner.setAdapter(recommendAdapter);
            mBinding.banner.setVisibility(View.VISIBLE);
            mBinding.ivRecord.setVisibility(View.GONE);
            mBinding.banner.startAutoPlay();
        });

        mModel.loadData();
    }

    private void showAnimSetting() {
        BannerSettingFragment bannerSettingDialog = new BannerSettingFragment();
        bannerSettingDialog.setParams(ViewProperty.getHomeBannerParams());
        bannerSettingDialog.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onParamsUpdated(BannerParams params) {

            }

            @Override
            public void onParamsSaved(BannerParams params) {
                ViewProperty.setHomeBannerParams(params);
                BannerHelper.setBannerParams(mBinding.banner, params);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(bannerSettingDialog);
        dialogFragment.setTitle("Banner Setting");
        dialogFragment.show(getSupportFragmentManager(), "BannerSettingFragment");
    }

    private void showRecommendSetting() {
        RecommendFragment content = new RecommendFragment();
        content.setBean(SettingProperty.getHomeRecBean());
        content.setOnRecommendListener(bean -> mModel.updateRecordFilter(bean));
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
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
            mBinding.banner.stopAutoPlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.startAutoPlay();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.stopAutoPlay();
        }
    }

}
