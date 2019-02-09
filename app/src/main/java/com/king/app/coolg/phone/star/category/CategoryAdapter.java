package com.king.app.coolg.phone.star.category;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterCategoryBinding;

public class CategoryAdapter extends BaseBindingAdapter<AdapterCategoryBinding, CategoryViewItem> {

    private boolean selectMode;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_category;
    }

    @Override
    protected void onBindItem(AdapterCategoryBinding binding, int position, CategoryViewItem bean) {
        binding.setBean(bean);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selectMode) {
            CategoryViewItem category = list.get(position);
            category.getObserver().onSelect(category);
        }
        else {
            super.onClickItem(v, position);
        }
    }

    public void setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        for (int i = 0; i < getItemCount(); i ++) {
            list.get(i).setSelected(false);
            list.get(i).setVisibility(selectMode ? View.VISIBLE:View.GONE);
        }
    }

    public void notifyCategoryChanged(CategoryViewItem item) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getCategory().getId() == item.getCategory().getId()) {
                notifyItemChanged(i);
                break;
            }
        }
    }
}
