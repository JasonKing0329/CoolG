package com.king.app.coolg.phone.video.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoStarPlayListBinding;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.player.PlayerActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/2/25 11:32
 */
@Route("PlayStarList")
public class PlayStarListActivity extends MvvmActivity<ActivityVideoStarPlayListBinding, PlayStarListViewModel> {

    public static final String EXTRA_STAR_ID = "star_id";

    private PlayerItemAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_video_star_play_list;
    }

    private long getStarId() {
        return getIntent().getLongExtra(EXTRA_STAR_ID, -1);
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);

        //set global configuration: turn on multiple_requests
        PlayerManager.getInstance().getDefaultVideoInfo().addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "multiple_requests", 1L));

        if (ScreenUtils.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            mBinding.tvTotal.setVisibility(View.GONE);

            mBinding.rvVideos.setLayoutManager(new GridLayoutManager(this, 3));
            mBinding.rvVideos.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildLayoutPosition(view);
                    outRect.top = ScreenUtils.dp2px(10);
                    if (position % 3 == 0) {
                        outRect.left = ScreenUtils.dp2px(8);
                    }
                    else if (position % 3 == 1) {
                        outRect.left = ScreenUtils.dp2px(8);
                    }
                    else {
                        outRect.left = ScreenUtils.dp2px(8);
                        outRect.right = ScreenUtils.dp2px(8);
                    }
                }
            });
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBinding.rvVideos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mBinding.rvVideos.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            });
        }

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_play_sequence:
                    playList(false);
                    break;
                case R.id.menu_play_random:
                    playList(true);
                    break;
            }
        });
        mBinding.actionbar.updateMenuItemVisible(R.id.menu_clear, false);
    }

    private void playList(boolean isRandom) {
        Router.build("Player")
                .with(PlayerActivity.EXTRA_STAR_ID, getStarId())
                .with(PlayerActivity.EXTRA_PLAY_RANDOM, isRandom)
                .go(this);
    }

    @Override
    protected PlayStarListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayStarListViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.starObserver.observe(this, star -> {});
        mModel.itemsObserver.observe(this, list -> showItems(list));

        mModel.loadPlayItems(getStarId());
    }

    private void showItems(List<PlayItemViewBean> list) {
        if (adapter == null) {
            adapter = new PlayerItemAdapter();
            adapter.setEnableDelete(false);
            adapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            adapter.setOnPlayEmptyUrlListener((fingerprint, callback) -> {
                int position = Integer.parseInt(fingerprint);
                mModel.getPlayUrl(position, callback);
            });
            adapter.setList(list);
            mBinding.rvVideos.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }

    private void goToRecordPage(Record record) {
        if (record == null) {
            showMessageShort("record is null");
            return;
        }
        Router.build("RecordPad")
                .with(RecordActivity.EXTRA_RECORD_ID, record.getId())
                .go(this);
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
