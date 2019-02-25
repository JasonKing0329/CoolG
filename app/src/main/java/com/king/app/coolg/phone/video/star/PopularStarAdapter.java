package com.king.app.coolg.phone.video.star;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPopularStarItemBinding;
import com.king.app.coolg.phone.video.home.VideoGuy;

public class PopularStarAdapter extends BaseBindingAdapter<AdapterPopularStarItemBinding, VideoGuy> {

    private boolean isMultiSelect;

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
        for (int i = 0; i < getItemCount(); i ++) {
            list.get(i).setVisibility(multiSelect ? View.VISIBLE:View.GONE);
        }
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_popular_star_item;
    }

    @Override
    protected void onBindItem(AdapterPopularStarItemBinding binding, int position, VideoGuy bean) {
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
