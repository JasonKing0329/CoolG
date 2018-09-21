package com.king.app.coolg.phone.studio.page;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.ItemDataBinder;
import com.king.app.coolg.databinding.AdapterStudioPageInfoBinding;

/**
 * Desc: 混合布局里的信息栏布局，关联实体StudioPageItem
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:00
 */
public class InfoAdapter extends ItemDataBinder<StudioPageItem, AdapterStudioPageInfoBinding> {
    @Override
    protected void bindModel(StudioPageItem item, AdapterStudioPageInfoBinding binding) {
        binding.tvVideos.setText(item.getStrCount());
        binding.tvHigh.setText(item.getStrHighCount());
        binding.tvTime.setText(item.getUpdateTime());
    }

    @Override
    protected AdapterStudioPageInfoBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return AdapterStudioPageInfoBinding.inflate(inflater);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof StudioPageItem;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }
}
