package com.king.app.coolg.phone.video.home;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPhoneBinding;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.phone.video.list.PlayListActivity;
import com.king.app.coolg.phone.video.list.PlayStarListActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.lib.banner.BannerFlipStyleProvider;

import java.util.ArrayList;

import tcking.github.com.giraffeplayer2.PlayerManager;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 15:26
 */
@Route("VideoHomePhone")
public class VideoHomePhoneActivity extends MvvmActivity<ActivityVideoPhoneBinding, VideoHomeViewModel> {

    private final int REQUEST_INSERT_TO_PLAY_ORDER = 6051;
    private final int REQUEST_SET_PLAY_ORDER = 6052;
    private final int REQUEST_ENTER_PLAY_ORDER = 6053;

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

        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_refresh:
                    if (mBinding.banner.isEnableSwitch()) {
                        mModel.loadRecommend();
                    }
                    else {
                        showMessageShort("Please stop video first!");
                    }
                    break;
                case R.id.menu_recommend_setting:
                    RecommendFragment content = new RecommendFragment();
                    content.setOnRecommendListener(bean -> mModel.updateRecommend(bean));
                    DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
                    dialogFragment.setTitle("Recommend Setting");
                    dialogFragment.setContentFragment(content);
                    dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
                    dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
                    break;
            }
        });

        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvItems.setEnableLoadMore(true);
        mBinding.rvItems.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildLayoutPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(8);
                }
            }
        });
        // 不自动加载更多
//        mBinding.rvItems.setOnLoadMoreListener(() -> mModel.loadMore());

        // viewpager切换效果
        BannerFlipStyleProvider.setPagerAnim(mBinding.banner, 3);
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
                        .requestCode(REQUEST_SET_PLAY_ORDER)
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onPlayList() {
                Router.build("PlayOrder")
                        .requestCode(REQUEST_ENTER_PLAY_ORDER)
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
                mModel.loadMore();
            }

            @Override
            public void onClickItem(int position, PlayItemViewBean bean) {
                Router.build("RecordPhone")
                        .with(RecordActivity.EXTRA_RECORD_ID, bean.getRecord().getId())
                        .go(VideoHomePhoneActivity.this);
            }

            @Override
            public void onAddToVideoOrder(PlayItemViewBean bean) {
                mModel.saveItemToAddOrder(bean);
                Router.build("PlayOrder")
                        .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                        .requestCode(REQUEST_INSERT_TO_PLAY_ORDER)
                        .go(VideoHomePhoneActivity.this);
            }
        });
        adapter.setOnPlayEmptyUrlListener((fingerprint, callback) -> {
            int position = Integer.parseInt(fingerprint);
            mModel.getRecentPlayUrl(position, callback);
        });
        mBinding.rvItems.setAdapter(adapter);

        mModel.headDataObserver.observe(this, data -> {
            adapter.setHeadData(data);
            adapter.notifyDataSetChanged();
        });
        mModel.recentVideosObserver.observe(this, list -> {
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
                    Router.build("RecordPhone")
                            .with(RecordActivity.EXTRA_RECORD_ID, item.getRecord().getId())
                            .go(VideoHomePhoneActivity.this);
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
        else if (requestCode == REQUEST_INSERT_TO_PLAY_ORDER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.insertToPlayList(list);
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
