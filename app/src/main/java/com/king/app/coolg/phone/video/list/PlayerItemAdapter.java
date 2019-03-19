package com.king.app.coolg.phone.video.list;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPlayItemBinding;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.widget.video.OnPlayEmptyUrlListener;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 14:49
 */
public class PlayerItemAdapter extends BaseBindingAdapter<AdapterPlayItemBinding, PlayItemViewBean> {

    private OnPlayItemListener onPlayItemListener;
    private boolean enableDelete = true;

    private int instantPlayIndex;

    private OnPlayEmptyUrlListener onPlayEmptyUrlListener;

    public void setOnPlayItemListener(OnPlayItemListener onPlayItemListener) {
        this.onPlayItemListener = onPlayItemListener;
    }

    public void setOnPlayEmptyUrlListener(OnPlayEmptyUrlListener onPlayEmptyUrlListener) {
        this.onPlayEmptyUrlListener = onPlayEmptyUrlListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_play_item;
    }

    @Override
    protected void onBindItem(AdapterPlayItemBinding binding, int position, PlayItemViewBean bean) {
        if (bean.getRecord() != null) {
            binding.tvName.setText(bean.getRecord().getName());
        }
        binding.ivDelete.setVisibility(enableDelete ? View.VISIBLE:View.GONE);
        binding.videoView.setFingerprint(position);
        binding.videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        // 横屏下必须设置这个，否则从全屏返回会激活屏幕为portrait方向（GiraffePlayer源码的setDisplayModel(DISPLAY_NORMAL)可以看出从全屏到非全屏执行了请求屏幕方向SCREEN_ORIENTATION_PORTRAIT）
        if (ScreenUtils.isTablet()) {
            binding.videoView.getVideoInfo().setPortraitWhenFullScreen(false);
        }
        GlideApp.with(binding.videoView.getContext())
                .load(bean.getCover())
                .error(R.drawable.def_small)
                .into(binding.videoView.getCoverView());
        if (!TextUtils.isEmpty(bean.getPlayUrl())) {
            binding.videoView.setVideoPath(bean.getPlayUrl());
            binding.videoView.prepare();
        }
        binding.videoView.setOnPlayEmptyUrlListener(onPlayEmptyUrlListener);
        binding.ivDelete.setOnClickListener(v -> onPlayItemListener.onDeleteItem(position, bean));
        binding.ivPlay.setOnClickListener(v -> onPlayItemListener.onPlayItem(position, bean));
    }

    public void setEnableDelete(boolean enableDelete) {
        this.enableDelete = enableDelete;
    }

    public void readyToPlay(PlayItemViewBean item) {
    }

    public interface OnPlayItemListener {
        void onPlayItem(int position, PlayItemViewBean bean);
        void onDeleteItem(int position, PlayItemViewBean bean);
    }
}
