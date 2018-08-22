package com.king.app.coolg.pad.record;

import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordGalleryBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:28
 */
public class RecordGalleryAdapter extends BaseBindingAdapter<AdapterRecordGalleryBinding, String> {

    private int selection = -1;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_gallery;
    }

    @Override
    protected void onBindItem(AdapterRecordGalleryBinding binding, int position, String bean) {
        GlideApp.with(binding.ivItem.getContext())
                .load(list.get(position))
                .error(R.drawable.default_cover)
                .into(binding.ivItem);

        binding.groupItem.setSelected(position == selection);
    }

    public void updateSelection(int selection) {
        if (selection != this.selection) {
            int lastSelection = this.selection;
            this.selection = selection;
            if (lastSelection != -1) {
                notifyItemChanged(lastSelection);
            }
            notifyItemChanged(selection);
        }
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selection != position) {
            updateSelection(position);
            super.onClickItem(v, position);
        }
    }
}
