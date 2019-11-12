package com.king.app.coolg.phone.video.player;

import android.graphics.Color;
import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPlaylistItemBinding;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/16 11:30
 */
public class PlayListAdapter extends BaseBindingAdapter<AdapterPlaylistItemBinding, PlayItemViewBean> {

    private int mPlayIndex;

    private OnDeleteListener onDeleteListener;
    private boolean enableDelete;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_playlist_item;
    }

    public void setPlayIndex(int mPlayIndex) {
        this.mPlayIndex = mPlayIndex;
    }

    @Override
    protected void onBindItem(AdapterPlaylistItemBinding binding, int position, PlayItemViewBean bean) {
        // 只播放地址
        if (bean.getRecord() == null) {
            binding.ivThumb.setVisibility(View.GONE);
            binding.ivDelete.setVisibility(View.INVISIBLE);
            binding.tvName.setText(bean.getPlayUrl());
            binding.getRoot().setBackgroundColor(binding.getRoot().getContext().getResources().getColor(R.color.playlist_bg_focus));
        }
        // 播放record item
        else {
            binding.ivThumb.setVisibility(View.VISIBLE);

            GlideApp.with(binding.ivThumb.getContext())
                    .load(bean.getCover())
                    .error(R.drawable.def_small)
                    .into(binding.ivThumb);
            if (bean.getRecord() == null) {
                binding.tvName.setText("");
            }
            else {
                binding.tvName.setText(bean.getRecord().getName());
            }
            if (position == mPlayIndex) {
                binding.getRoot().setBackgroundColor(binding.getRoot().getContext().getResources().getColor(R.color.playlist_bg_focus));
            }
            else {
                binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
            }
            binding.ivDelete.setVisibility(enableDelete ? View.VISIBLE:View.GONE);
            binding.ivDelete.setOnClickListener(v -> onDeleteListener.onDelete(position, bean));
        }
    }

    public void enableDelete(boolean enableDelete) {
        this.enableDelete = enableDelete;
    }

    public interface OnDeleteListener {
        void onDelete(int position, PlayItemViewBean bean);
    }
}
