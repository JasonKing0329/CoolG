package com.king.app.coolg.phone.record;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordPlayOrderBinding;
import com.king.app.coolg.phone.video.home.VideoPlayList;

public class RecordPlayOrdersAdapter extends BaseBindingAdapter<AdapterRecordPlayOrderBinding, VideoPlayList> {

    private boolean deleteMode;

    private OnDeleteListener onDeleteListener;

    public RecordPlayOrdersAdapter() {
        setOnItemLongClickListener((view, position, data) -> {
            toggleDeleteMode();
            notifyDataSetChanged();
        });
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void toggleDeleteMode() {
        deleteMode = !deleteMode;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_play_order;
    }

    @Override
    protected void onBindItem(AdapterRecordPlayOrderBinding binding, int position, VideoPlayList bean) {
        binding.setBean(bean);
        if (deleteMode) {
            binding.ivDelete.setVisibility(View.VISIBLE);
            binding.ivDelete.setOnClickListener(v -> {
                if (onDeleteListener != null) {
                    onDeleteListener.onDeleteOrder(bean);
                }
            });
        }
        else {
            binding.ivDelete.setVisibility(View.GONE);
            binding.ivDelete.setOnClickListener(null);
        }
    }

    public interface OnDeleteListener {
        void onDeleteOrder(VideoPlayList order);
    }
}
