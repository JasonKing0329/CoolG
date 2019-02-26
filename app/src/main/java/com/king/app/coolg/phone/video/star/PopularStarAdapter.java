package com.king.app.coolg.phone.video.star;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterPopularStarItemBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.coolg.utils.ScreenUtils;

public class PopularStarAdapter extends BaseBindingAdapter<AdapterPopularStarItemBinding, VideoGuy> {

    private boolean isMultiSelect;
    private int mViewType;

    public PopularStarAdapter(){
        mViewType = SettingProperty.getVideoStarOrderViewType();
    }

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
        if (mViewType == PreferenceValue.VIEW_TYPE_GRID) {
            binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            binding.tvVideos.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.tvVideos.getLayoutParams();
            params.setMarginEnd(ScreenUtils.dp2px(8));
            params.bottomMargin = ScreenUtils.dp2px(8);
            binding.tvVideos.setLayoutParams(params);
        }
        else {
            binding.tvName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            binding.tvVideos.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.tvVideos.getLayoutParams();
            params.setMarginEnd(ScreenUtils.dp2px(16));
            params.bottomMargin = ScreenUtils.dp2px(16);
            binding.tvVideos.setLayoutParams(params);
        }
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

    public void setViewType(int type) {
        mViewType = type;
    }
}
