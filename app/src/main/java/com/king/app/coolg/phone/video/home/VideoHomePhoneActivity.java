package com.king.app.coolg.phone.video.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPhoneBinding;
import com.king.app.coolg.phone.video.list.PlayListActivity;
import com.king.app.coolg.phone.video.list.PlayStarListActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;

import java.util.ArrayList;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 15:26
 */
@Route("VideoHomePhone")
public class VideoHomePhoneActivity extends MvvmActivity<ActivityVideoPhoneBinding, VideoHomeViewModel> {

    public static final int REQUEST_SELECT_ORDER = 6052;

    private HomeAdapter adapter;

    private VideoRecAdapter recAdapter;

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
        adapter = new HomeAdapter();
        adapter.setOnHeadActionListener(new HomeAdapter.OnHeadActionListener() {
            @Override
            public void onSetPlayList() {
                Router.build("PlayOrder")
                        .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                        .requestCode(REQUEST_SELECT_ORDER)
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onPlayList() {
                Router.build("PlayOrder")
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onClickPlayList(VideoPlayList order) {
                Router.build("PlayList")
                        .with(PlayListActivity.EXTRA_ORDER_ID, order.getPlayOrder().getId())
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onRefreshGuy() {
                mModel.loadHeadData();
            }

            @Override
            public void onGuy() {
                Router.build("PopularStar")
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onClickGuy(VideoGuy guy) {
                Router.build("PlayStarList")
                        .with(PlayStarListActivity.EXTRA_STAR_ID, guy.getStar().getId())
                        .go(VideoHomePhoneActivity.this);
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
        mBinding.rvItems.setAdapter(adapter);

        mModel.headDataObserver.observe(this, data -> {
            adapter.setHeadData(data);
            adapter.notifyDataSetChanged();
        });
        mModel.itemsObserver.observe(this, list -> {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        });
        // 获取url失败，重新启动轮播
        mModel.getPlayUrlFailed.observe(this, failed -> {
            mBinding.banner.startAutoPlay();
            mBinding.banner.setEnableSwitch(true);
        });
        mModel.recommendObserver.observe(this, list -> {
            mBinding.banner.stopAutoPlay();

            recAdapter = new VideoRecAdapter(mBinding.banner);
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
            });
            mBinding.banner.setBannerAdapter(recAdapter);

            mBinding.banner.startAutoPlay();
        });
        mModel.buildPage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_ORDER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.updateVideoCoverPlayList(list);
            }
        }
    }
}
