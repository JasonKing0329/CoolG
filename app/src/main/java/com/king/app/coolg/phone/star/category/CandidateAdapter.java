package com.king.app.coolg.phone.star.category;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.TwoTypeBindingAdapter;
import com.king.app.coolg.databinding.AdapterCategoryAddBinding;
import com.king.app.coolg.databinding.AdapterCategoryStarBinding;

public class CandidateAdapter extends TwoTypeBindingAdapter<AdapterCategoryStarBinding, AdapterCategoryAddBinding, CategoryStar, CategoryAdd> {

    private boolean isDeleteMode;

    private OnDeleteListener onDeleteListener;

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    @Override
    protected Class getType1Class() {
        return CategoryStar.class;
    }

    @Override
    protected Class getType2Class() {
        return CategoryAdd.class;
    }

    @Override
    protected int getType1LayoutRes() {
        return R.layout.adapter_category_star;
    }

    @Override
    protected int getType2LayoutRes() {
        return R.layout.adapter_category_add;
    }

    @Override
    protected void onBindType1(AdapterCategoryStarBinding binding, int position, CategoryStar bean) {
        binding.setBean(bean);
        binding.ivDelete.setVisibility(isDeleteMode ? View.VISIBLE:View.GONE);
        binding.ivDelete.setOnClickListener(view -> onDeleteListener.onDeleteCandidate(position, bean));
    }

    @Override
    protected void onBindType2(AdapterCategoryAddBinding binding, int position, CategoryAdd bean) {

    }

    public void notifyStarRemoved(CategoryStar star) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i) == star) {
                list.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public interface OnDeleteListener {
        void onDeleteCandidate(int position, CategoryStar star);
    }
}
