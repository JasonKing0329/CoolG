package com.king.app.coolg.phone.video.player;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoPlayerBinding;
import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.view.widget.video.OnVideoDurationListener;
import com.king.app.coolg.view.widget.video.OnVideoListListener;
import com.king.app.coolg.view.widget.video.OnVideoListener;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 16:37
 */
@Route("Player")
public class PlayerActivity extends MvvmActivity<ActivityVideoPlayerBinding, PlayerViewModel> {

    private PlayListFragment ftList;

    public static final String EXTRA_AUTO_PLAY = "auto_play";

    @Override
    protected int getContentView() {
        return R.layout.activity_video_player;
    }

    @Override
    protected void initView() {
        hideBottomUIMenu();

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
            public void onStart() {

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
        mBinding.videoView.setOnVideoDurationListener(new OnVideoDurationListener() {
            @Override
            public void onReceiveDuration(int duration) {
                mModel.updateDuration(duration);
            }
        });
        mBinding.videoView.setOnVideoListListener(new OnVideoListListener() {
            @Override
            public void playNext() {
                mModel.updatePlayToDb();
                mModel.playNext();
            }

            @Override
            public void playPrevious() {
                mModel.updatePlayToDb();
                mModel.playPrevious();
            }

            @Override
            public void showPlayList() {
                showList();
            }
        });
        mBinding.videoView.setOnVideoClickListener(() -> dismissPlayList());
        mBinding.videoView.setOnPlayEmptyUrlListener((fingerprint, callback) -> mModel.loadPlayUrl(callback));
        mBinding.videoView.prepare();
    }
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);

        }
    }

    private boolean isInitAutoPlay() {
        return getIntent().getBooleanExtra(EXTRA_AUTO_PLAY, true);
    }

    @Override
    protected PlayerViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.closeListObserver.observe(this, close -> dismissPlayList());
        mModel.prepareVideo.observe(this, bean -> prepareItem(bean));
        mModel.playVideo.observe(this, bean -> playItem(bean));
        mModel.stopVideoObserver.observe(this, stop -> mBinding.videoView.pause());
        mModel.videoUrlIsReady.observe(this, bean -> urlIsReady(bean));

        mModel.loadPlayItems(isInitAutoPlay());
    }

    private void dismissPlayList() {
        if (mBinding.ftList.getVisibility() != View.GONE) {
            mBinding.ftList.startAnimation(listDisappear());
        }
    }

    private void prepareItem(PlayList.PlayItem bean) {
        if (bean.getUrl() != null) {
            mBinding.videoView.getVideoInfo().setBgColor(Color.BLACK).setShowTopBar(true).setTitle(mModel.getVideoName(bean));
            // 通知立即刷新标题
            mBinding.videoView.refreshTitle();
            mBinding.videoView.setVideoPath(bean.getUrl());
        }
    }

    private void playItem(PlayList.PlayItem bean) {
        if (bean.getUrl() == null) {
            mModel.loadPlayUrl(bean);
        }
        else {
            mBinding.videoView.play();
        }
    }

    private void urlIsReady(PlayList.PlayItem bean) {
        mBinding.videoView.getVideoInfo().setBgColor(Color.BLACK).setShowTopBar(true).setTitle(mModel.getVideoName(bean));
        // 通知立即刷新标题
        mBinding.videoView.refreshTitle();
        mBinding.videoView.setVideoPath(bean.getUrl());
        mBinding.videoView.play();
    }


    @Override
    protected void onDestroy() {
        if (mModel != null) {
            mModel.updatePlayToDb();
        }
        PlayListInstance.getInstance().destroy();
        super.onDestroy();
    }

    private void showList() {
        if (ftList == null) {
            ftList = new PlayListFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.ft_list, ftList, "PlayListFragment")
                    .commit();
        }
        mBinding.ftList.startAnimation(listAppear());
    }

    private Animation listAppear() {
        mBinding.ftList.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());

        TranslateAnimation translate = new TranslateAnimation(mBinding.ftList.getWidth(), 0, 0, 0);
        set.addAnimation(translate);
        ScaleAnimation scale = new ScaleAnimation(0, 1, 1, 1);
        set.addAnimation(scale);
        return set;
    }

    private Animation listDisappear() {
        AnimationSet set = new AnimationSet(true);
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.ftList.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        TranslateAnimation translate = new TranslateAnimation(0, mBinding.ftList.getWidth(), 0, 0);
        set.addAnimation(translate);
        ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 1);
        set.addAnimation(scale);
        return set;
    }
}
