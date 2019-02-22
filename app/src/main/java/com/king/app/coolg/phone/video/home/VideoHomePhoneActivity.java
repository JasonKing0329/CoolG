package com.king.app.coolg.phone.video.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPhoneBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.list.StarSelectorActivity;
import com.king.app.coolg.utils.LMBannerViewUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 15:26
 */
public class VideoHomePhoneActivity extends MvvmActivity<ActivityVideoPhoneBinding, VideoHomeViewModel> {

    public static final int REQUEST_SELECT_STAR = 6051;

    private HomeAdapter adapter;

    @Override
    protected VideoHomeViewModel createViewModel() {
        return ViewModelProviders.of(this).get(VideoHomeViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_phone;
    }

    @Override
    protected void initView() {

        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvItems.setEnableLoadMore(true);
//        mBinding.rvItems.setOnLoadMoreListener(() -> mModel.loadMore());

        setBannerParams();
    }

    @Override
    protected void initData() {
        adapter = new HomeAdapter();
        adapter.setOnHeadActionListener(new HomeAdapter.OnHeadActionListener() {
            @Override
            public void onSetPlayList() {

            }

            @Override
            public void onPlayList() {

            }

            @Override
            public void onClickPlayList(VideoPlayList order) {

            }

            @Override
            public void onSetGuy() {
                Router.build("StarSelector")
                        .with(StarSelectorActivity.EXTRA_LIMIT_MAX, 4)
                        .requestCode(REQUEST_SELECT_STAR)
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onGuy() {

            }

            @Override
            public void onClickGuy(VideoGuy guy) {

            }
        });
        adapter.setOnListListener(new HomeAdapter.OnListListener() {
            @Override
            public void onLoadMore() {

            }

            @Override
            public void onClickItem(View view, VideoItem record) {

            }
        });

        mModel.buildPage();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_STAR ) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(StarSelectorActivity.RESP_SELECT_RESULT);
                mModel.updateVideoCoverStar(list);
            }
        }
    }
}
