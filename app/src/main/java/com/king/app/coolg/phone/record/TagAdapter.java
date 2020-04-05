package com.king.app.coolg.phone.record;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterTagItemBinding;
import com.king.app.gdb.data.entity.Tag;

public class TagAdapter extends BaseBindingAdapter<AdapterTagItemBinding, Tag> {

    private boolean showDelete;

    private OnDeleteListener onDeleteListener;

    private int selection = -1;

    public void setShowDelete(boolean showDelete) {
        this.showDelete = showDelete;
    }

    public void toggleDelete() {
        showDelete = !showDelete;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_tag_item;
    }

    @Override
    protected void onBindItem(AdapterTagItemBinding binding, int position, Tag bean) {
        binding.tvName.setText(bean.getName());
        binding.ivRemove.setVisibility(showDelete ? View.VISIBLE:View.GONE);
        binding.ivRemove.setOnClickListener(v -> onDeleteListener.onDelete(position, bean));
        binding.tvName.setSelected(position == selection);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (position != selection) {
            super.onClickItem(v, position);
        }
        selection = position;
        notifyDataSetChanged();
    }

    public interface OnDeleteListener {
        void onDelete(int position, Tag bean);
    }
}
