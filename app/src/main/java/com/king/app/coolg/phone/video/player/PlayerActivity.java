package com.king.app.coolg.phone.video.player;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
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
        mBinding.videoView.setOnPlayEmptyUrlListener((fingerprint, callback) -> mModel.loadPlayUrl(callback));
        mBinding.videoView.prepare();
    }

    @Override
    protected PlayerViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayerViewModel.class);
    }

    @Override
    protected void initData() {

        mModel.closeListObserver.observe(this, close -> mBinding.ftList.startAnimation(listDisappear()));
        mModel.videoObserver.observe(this, bean -> playItem(bean));
        mModel.stopVideoObserver.observe(this, stop -> mBinding.videoView.pause());
        mModel.videoUrlIsReady.observe(this, bean -> urlIsReady(bean));

        mModel.loadPlayItems();
    }

    private void playItem(PlayList.PlayItem bean) {
        if (bean.getUrl() == null) {
            mModel.loadPlayUrl(bean);
        }
        else {
            urlIsReady(bean);
        }
    }

    private void urlIsReady(PlayList.PlayItem bean) {
        mBinding.videoView.getVideoInfo().setBgColor(Color.BLACK).setShowTopBar(true).setTitle(mModel.getVideoName(bean));
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
            listAppear();
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
