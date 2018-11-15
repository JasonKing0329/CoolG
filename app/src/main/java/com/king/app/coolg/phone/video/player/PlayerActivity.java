package com.king.app.coolg.phone.video.player;

import android.arch.lifecycle.ViewModelProviders;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPlayerBinding;
import com.king.app.coolg.phone.video.PlayItemViewBean;
import com.king.app.coolg.view.widget.video.OnVideoListener;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 16:37
 */
@Route("Player")
public class PlayerActivity extends MvvmActivity<ActivityVideoPlayerBinding, PlayerViewModel> {

    public static final String EXTRA_ORDER_ID = "order_id";
    public static final String EXTRA_PLAY_RANDOM = "play_random";

    @Override
    protected int getContentView() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        mBinding.videoView.setOnVideoListener(new OnVideoListener() {
            @Override
            public int getStartSeek() {
                return mModel.getStartSeek();
            }

            @Override
            public void updatePlayPosition(int currentPosition) {
                mModel.updatePlayPosition(currentPosition);
            }

            @Override
            public void onPlayComplete() {
                mModel.resetPlayInDb();
                mModel.playNext();
            }

            @Override
            public void onPause() {
                mModel.updatePlayToDb();
            }

            @Override
            public void onDestroy() {
                mModel.updatePlayToDb();
            }
        });
        mBinding.videoView.prepare();
    }

    @Override
    protected PlayerViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerViewModel.class);
    }

    private long getOrderId() {
        return getIntent().getLongExtra(EXTRA_ORDER_ID, -1);
    }

    @Override
    protected void initData() {

        mModel.itemsObserver.observe(this, list -> showList(list));
        mModel.videoObserver.observe(this, bean -> playItem(bean));

        mModel.loadPlayItems(getOrderId());
    }

    private void playItem(PlayItemViewBean bean) {
        mBinding.videoView.setVideoPath(bean.getPlayItem().getUrl());
        mBinding.videoView.play();
    }

    private void showList(List<PlayItemViewBean> list) {
    }
}
