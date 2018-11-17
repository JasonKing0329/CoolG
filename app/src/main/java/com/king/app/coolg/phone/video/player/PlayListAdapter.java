package com.king.app.coolg.phone.video.player;

import android.graphics.Color;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPlaylistItemBinding;
import com.king.app.coolg.phone.video.PlayItemViewBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/16 11:30
 */
public class PlayListAdapter extends BaseBindingAdapter<AdapterPlaylistItemBinding, PlayItemViewBean> {

    private int mPlayIndex;

    private OnDeleteListener onDeleteListener;

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
        GlideApp.with(binding.ivThumb.getContext())
                .load(bean.getCover())
                .error(R.drawable.def_small)
                .into(binding.ivThumb);
        if (bean.getPlayItem().getRecord() == null) {
            binding.tvName.setText("");
        }
        else {
            binding.tvName.setText(bean.getPlayItem().getRecord().getName());
        }
        if (position == mPlayIndex) {
            binding.getRoot().setBackgroundColor(binding.getRoot().getContext().getResources().getColor(R.color.playlist_bg_focus));
        }
        else {
            binding.getRoot().setBackgroundColor(Color.TRANSPARENT);
        }
        binding.ivDelete.setOnClickListener(v -> onDeleteListener.onDelete(position, bean));
    }

    public interface OnDeleteListener {
        void onDelete(int position, PlayItemViewBean bean);
    }
}
