package com.king.app.coolg.phone.video.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.widget.video.EmbedVideoView;
import com.king.app.coolg.view.widget.video.OnPlayEmptyUrlListener;
import com.king.app.coolg.view.widget.video.OnVideoListener;
import com.king.lib.banner.CoolBannerAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/25 14:49
 */
public class VideoRecAdapter extends CoolBannerAdapter<PlayItemViewBean> {

    private OnPlayEmptyUrlListener onPlayEmptyUrlListener;

    private OnPlayListener onPlayListener;

    /**
     * 是否截获全屏事件
     */
    private boolean interceptFullScreen;

    public void setOnPlayEmptyUrlListener(OnPlayEmptyUrlListener onPlayEmptyUrlListener) {
        this.onPlayEmptyUrlListener = onPlayEmptyUrlListener;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.adapter_video_recommend;
    }

    public void setInterceptFullScreen(boolean interceptFullScreen) {
        this.interceptFullScreen = interceptFullScreen;
    }

    @Override
    protected void onBindView(View view, int position, PlayItemViewBean bean) {
        EmbedVideoView videoView = view.findViewById(R.id.video_view);
        TextView tvRank = view.findViewById(R.id.tv_rank);
        PlayItemViewBean item = list.get(position);
        videoView.setFingerprint(position);
        videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        videoView.getCoverView().setOnClickListener(v -> onPlayListener.onClickPlayItem(item));
        // 横屏下必须设置这个，否则从全屏返回会激活屏幕为portrait方向（GiraffePlayer源码的setDisplayModel(DISPLAY_NORMAL)可以看出从全屏到非全屏执行了请求屏幕方向SCREEN_ORIENTATION_PORTRAIT）
        if (ScreenUtils.isTablet()) {
            videoView.getVideoInfo().setPortraitWhenFullScreen(false);
        }
        GlideApp.with(videoView.getContext())
                .load(item.getCover())
                .placeholder(R.drawable.def_small)
                .error(R.drawable.def_small)
                .into(videoView.getCoverView());
        if (!TextUtils.isEmpty(item.getPlayUrl())) {
            videoView.setVideoPath(item.getPlayUrl());
            videoView.prepare();
        }
        videoView.setOnPlayEmptyUrlListener(onPlayEmptyUrlListener);
        if (interceptFullScreen) {
            videoView.interceptFullScreenListener(v -> onPlayListener.onInterceptFullScreen(bean));
        }
        videoView.setOnVideoListener(new OnVideoListener() {
            @Override
            public int getStartSeek() {
                return 0;
            }

            @Override
            public void updatePlayPosition(int currentPosition) {

            }

            @Override
            public void onPlayComplete() {
                onPlayListener.onPausePlay();
            }

            @Override
            public void onPause() {
                onPlayListener.onPausePlay();
            }

            @Override
            public void onDestroy() {
                onPlayListener.onPausePlay();
            }

            @Override
            public void onStart() {
                onPlayListener.onStartPlay();
            }
        });

        TextView testView = view.findViewById(R.id.tv_index);
        testView.setText(position + "--" + item.getName());

        if (bean.getRecord().getCountRecord() != null) {
            tvRank.setText("R-" + bean.getRecord().getCountRecord().getRank());
        }
    }

    public interface OnPlayListener {
        void onStartPlay();
        void onPausePlay();
        void onClickPlayItem(PlayItemViewBean item);
        void onInterceptFullScreen(PlayItemViewBean item);
    }

}
