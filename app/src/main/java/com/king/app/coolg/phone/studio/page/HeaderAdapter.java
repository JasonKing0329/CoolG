package com.king.app.coolg.phone.studio.page;

import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.ItemDataBinder;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.AdapterStudioPageHeadBinding;

/**
 * Desc: 混合布局里的标题栏布局，关联实体String
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:00
 */
public class HeaderAdapter extends ItemDataBinder<String, AdapterStudioPageHeadBinding> {

    private OnSeeAllListener onSeeAllListener;

    public void setOnSeeAllListener(OnSeeAllListener onSeeAllListener) {
        this.onSeeAllListener = onSeeAllListener;
    }

    @Override
    protected void bindModel(String item, AdapterStudioPageHeadBinding binding) {
        binding.tvHead.setText(item);
        binding.tvAll.setOnClickListener(v -> onSeeAllListener.onSeeAll());
    }

    @Override
    protected AdapterStudioPageHeadBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return DataBindingUtil.inflate(inflater, R.layout.adapter_studio_page_head, parent, false);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof String;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }

    public interface OnSeeAllListener {
        void onSeeAll();
    }
}
