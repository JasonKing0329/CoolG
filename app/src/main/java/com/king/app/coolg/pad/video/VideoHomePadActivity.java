package com.king.app.coolg.pad.video;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPadBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.coolg.phone.video.home.VideoRecAdapter;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.phone.video.list.PlayListActivity;
import com.king.app.coolg.phone.video.list.PlayStarListActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.lib.banner.BannerFlipStyleProvider;

import java.util.ArrayList;

import tcking.github.com.giraffeplayer2.PlayerManager;

@Route("VideoHomePad")
public class VideoHomePadActivity extends MvvmActivity<ActivityVideoPadBinding, VideoHomePadViewModel> {

    private final int REQUEST_SET_PLAY_ORDER = 6052;
    private final int REQUEST_ENTER_PLAY_ORDER = 6053;

    private VideoRecAdapter recAdapter;

    @Override
    protected VideoHomePadViewModel createViewModel() {
        return ViewModelProviders.of(this).get(VideoHomePadViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_pad;
    }

    @Override
    protected void initView() {
        BannerHelper.setBannerParams(mBinding.banner, ViewProperty.getVideoHomeBannerParams());

        mBinding.ivRefresh.setOnClickListener(v -> mModel.loadRecommend());

        mBinding.coverGuys.setOnClickListener(v -> {
            Router.build("PopularStar")
                    .go(getContext());
        });
        mBinding.ivGuys.setOnClickListener(v -> {
            mModel.loadHeadData();
        });
        mBinding.ivStar0.setOnClickListener(v -> onClickGuy(mModel.getGuy(0)));
        mBinding.ivStar1.setOnClickListener(v -> onClickGuy(mModel.getGuy(1)));
        mBinding.ivStar2.setOnClickListener(v -> onClickGuy(mModel.getGuy(2)));
        mBinding.ivStar3.setOnClickListener(v -> onClickGuy(mModel.getGuy(3)));

        mBinding.coverPlayList.setOnClickListener(v -> {
            Router.build("PlayOrder")
                    .requestCode(REQUEST_ENTER_PLAY_ORDER)
                    .go(getContext());
        });
        mBinding.ivPlayList.setOnClickListener(v -> {
            Router.build("PlayOrder")
                    .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                    .requestCode(REQUEST_SET_PLAY_ORDER)
                    .go(getContext());
        });
        mBinding.ivList0.setOnClickListener(v -> onClickPlayList(mModel.getPlayList(0)));
        mBinding.ivList1.setOnClickListener(v -> onClickPlayList(mModel.getPlayList(1)));
        mBinding.ivList2.setOnClickListener(v -> onClickPlayList(mModel.getPlayList(2)));
        mBinding.ivList3.setOnClickListener(v -> onClickPlayList(mModel.getPlayList(3)));

        mBinding.ivSetting.setOnClickListener(v -> {
            new AlertDialogFragment()
                    .setItems(new String[]{"Set banner anim", "Set recommend"}
                            , (dialogInterface, i) -> {
                                if (i == 0) {
                                    showBannerSetting();
                                }
                                else {
                                    showRecommendSetting();
                                }
                            }).show(getSupportFragmentManager(), "AlertDialogFragment");
        });
    }

    private void showBannerSetting() {
        BannerSettingFragment bannerSettingDialog = new BannerSettingFragment();
        bannerSettingDialog.setParams(ViewProperty.getVideoHomeBannerParams());
        bannerSettingDialog.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onParamsUpdated(BannerParams params) {

            }

            @Override
            public void onParamsSaved(BannerParams params) {
                ViewProperty.setVideoHomeBannerParams(params);
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
        content.setHideOnline(true);
        content.setBean(SettingProperty.getVideoRecBean());
        content.setOnRecommendListener(bean -> mModel.updateRecommend(bean));
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    private void onClickGuy(VideoGuy guy) {
        Router.build("PlayStarList")
                .with(PlayStarListActivity.EXTRA_STAR_ID, guy.getStar().getId())
                .go(getContext());
    }

    private void onClickPlayList(VideoPlayList order) {
        Router.build("PlayList")
                .with(PlayListActivity.EXTRA_ORDER_ID, order.getPlayOrder().getId())
                .go(getContext());
    }

    private Context getContext() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.banner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.banner.stopAutoPlay();
    }

    @Override
    protected void initData() {

        mModel.headDataObserver.observe(this, data -> {
            mBinding.setData(data);
        });
        mModel.recommendObserver.observe(this, list -> {
            mBinding.banner.stopAutoPlay();
            if (list.size() == 0) {
                showMessageShort("No video to recommend");
                mBinding.banner.setAdapter(null);
                return;
            }

            recAdapter = new VideoRecAdapter();
            recAdapter.setList(list);
            // 只要按下播放键就停止轮播
            // url尚未获取，需要先获取url
            recAdapter.setOnPlayEmptyUrlListener((fingerprint, callback) -> {
                mBinding.banner.stopAutoPlay();
                mBinding.banner.setEnableSwitch(false);
                int position = Integer.parseInt(fingerprint);
                mModel.getRecommendPlayUrl(position, callback);
            });
            recAdapter.setOnPlayListener(new VideoRecAdapter.OnPlayListener() {
                @Override
                public void onStartPlay() {
                    // 有可能是url已获取的情况按播放键直接播放了
                    mBinding.banner.stopAutoPlay();
                    mBinding.banner.setEnableSwitch(false);
                }

                @Override
                public void onPausePlay() {
                    mBinding.banner.startAutoPlay();
                    mBinding.banner.setEnableSwitch(true);
                }

                @Override
                public void onClickPlayItem(PlayItemViewBean item) {
                    Router.build("RecordPad")
                            .with(RecordActivity.EXTRA_RECORD_ID, item.getRecord().getId())
                            .go(getContext());
                }
            });
            mBinding.banner.setAdapter(recAdapter);

            mBinding.banner.startAutoPlay();
        });
        mModel.buildPage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SET_PLAY_ORDER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.updateVideoCoverPlayList(list);
            }
        }
        else if (requestCode == REQUEST_ENTER_PLAY_ORDER) {
            if (resultCode == RESULT_OK) {
                mModel.loadHeadData();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // videoView必须在manifest中所属activity指定
        // android:configChanges="orientation|screenSize",且其中两个参数缺一不可
        // 同时在onConfigurationChanged中加入相关代码。
        // 这样在点击全屏时才能顺畅地切换为全屏
        PlayerManager.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (PlayerManager.getInstance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

}
