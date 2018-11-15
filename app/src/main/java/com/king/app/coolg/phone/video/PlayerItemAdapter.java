package com.king.app.coolg.phone.video;

import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPlayItemBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 14:49
 */
public class PlayerItemAdapter extends BaseBindingAdapter<AdapterPlayItemBinding, PlayItemViewBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_play_item;
    }

    @Override
    protected void onBindItem(AdapterPlayItemBinding binding, int position, PlayItemViewBean bean) {
        binding.videoView.setVideoPath(bean.getPlayItem().getUrl());
        if (bean.getPlayItem().getRecord() != null) {
            binding.tvName.setText(bean.getPlayItem().getRecord().getName());
        }
        binding.videoView.setFingerprint(position);
        binding.videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideApp.with(binding.videoView.getContext())
                .load(bean.getCover())
                .error(R.drawable.def_small)
                .into(binding.videoView.getCoverView());
        binding.videoView.prepare();
    }

}
