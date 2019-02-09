package com.king.app.coolg.phone.star.category;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogAddCategoryBinding;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

public class AddCategoryFragment extends DraggableContentFragment<FragmentDialogAddCategoryBinding> {

    private OnAddCategoryListener onAddCategoryListener;

    private CategoryViewItem categoryViewItem;

    public void setOnAddCategoryListener(OnAddCategoryListener onAddCategoryListener) {
        this.onAddCategoryListener = onAddCategoryListener;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_add_category;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    public void setCategoryViewItem(CategoryViewItem categoryViewItem) {
        this.categoryViewItem = categoryViewItem;
    }

    @Override
    protected void initView() {
        mBinding.tvOk.setOnClickListener(v -> {
            onAddCategoryListener.onAddCategory(mBinding.etName.getText().toString(), mBinding.spType.getSelectedItemPosition());
            dismissAllowingStateLoss();
        });

        if (categoryViewItem != null) {
            mBinding.etName.setText(categoryViewItem.getCategory().getName());
            mBinding.spType.setSelection(categoryViewItem.getCategory().getType());
            mBinding.spType.setEnabled(false);
        }
    }

    public interface OnAddCategoryListener {
        void onAddCategory(String name, int type);
    }
}
