package com.king.app.coolg.phone.video.order;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPlayOrderItemBinding;
import com.king.app.coolg.phone.video.home.VideoPlayList;

public class PlayOrderAdapter extends BaseBindingAdapter<AdapterPlayOrderItemBinding, VideoPlayList> {

    private boolean isMultiSelect;

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
        for (int i = 0; i < getItemCount(); i ++) {
            list.get(i).setVisibility(multiSelect ? View.VISIBLE:View.GONE);
        }
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_play_order_item;
    }

    @Override
    protected void onBindItem(AdapterPlayOrderItemBinding binding, int position, VideoPlayList bean) {
        binding.setBean(bean);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (isMultiSelect) {
            list.get(position).setChecked(!list.get(position).isChecked());
        }
        else {
            super.onClickItem(v, position);
        }
    }
}
