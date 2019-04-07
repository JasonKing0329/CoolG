package com.king.app.coolg.view;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterSimpleTitleValueBinding;
import com.king.app.coolg.model.bean.TitleValueBean;

public class SimpleTitleValueAdapter extends BaseBindingAdapter<AdapterSimpleTitleValueBinding, TitleValueBean> {

    private boolean showDivider;

    public void setShowDivider(boolean showDivider) {
        this.showDivider = showDivider;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_simple_title_value;
    }

    @Override
    protected void onBindItem(AdapterSimpleTitleValueBinding binding, int position, TitleValueBean bean) {
        binding.setBean(bean);
        if (showDivider) {
            binding.divider.setVisibility(position == 0 ? View.GONE:View.VISIBLE);
        }
        else {
            binding.divider.setVisibility(View.GONE);
        }
    }
}
