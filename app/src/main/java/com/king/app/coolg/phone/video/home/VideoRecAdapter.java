package com.king.app.coolg.phone.video.home;

import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.coolg.view.widget.banner.BannerAdapter;
import com.king.app.coolg.view.widget.video.EmbedVideoView;
import com.king.app.coolg.view.widget.video.OnPlayEmptyUrlListener;
import com.king.app.coolg.view.widget.video.OnVideoListener;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/25 14:49
 */
public class VideoRecAdapter extends BannerAdapter {

    private List<PlayItemViewBean> list;

    private OnPlayEmptyUrlListener onPlayEmptyUrlListener;

    private OnPlayListener onPlayListener;

    public VideoRecAdapter(ViewPager viewPager) {
        super(viewPager);
    }

    public void setList(List<PlayItemViewBean> list) {
        this.list = list;
    }

    public void setOnPlayEmptyUrlListener(OnPlayEmptyUrlListener onPlayEmptyUrlListener) {
        this.onPlayEmptyUrlListener = onPlayEmptyUrlListener;
    }

    public void setOnPlayListener(OnPlayListener onPlayListener) {
        this.onPlayListener = onPlayListener;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    @Override
    protected View onCreateView() {
        View group = LayoutInflater.from(getContext()).inflate(R.layout.adapter_video_recommend, null);
        return group;
    }

    @Override
    protected void onBindItem(int position, View view) {
        EmbedVideoView videoView = view.findViewById(R.id.video_view);
        PlayItemViewBean item = list.get(position);
        videoView.setFingerprint(position);
        videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        videoView.getCoverView().setOnClickListener(v -> onPlayListener.onClickPlayItem(item));
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
    }

    public interface OnPlayListener {
        void onStartPlay();
        void onPausePlay();

        void onClickPlayItem(PlayItemViewBean item);
    }
}
