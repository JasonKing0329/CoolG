package com.king.app.coolg.pad.gallery;

import android.view.View;
import android.view.ViewGroup;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterGalleryItemBinding;

/**
 * @description:
 * @author：Jing
 * @date: 2020/7/11 18:49
 */
public class ThumbAdapter extends BaseBindingAdapter<AdapterGalleryItemBinding, ThumbBean> {

    private int selection = 0;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_gallery_item;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    @Override
    protected void onBindItem(AdapterGalleryItemBinding binding, int position, ThumbBean bean) {
        ViewGroup.LayoutParams params = binding.ivImage.getLayoutParams();
        params.height = bean.getThumbHeight();
        params.width = bean.getThumbWidth();
        binding.ivImage.setLayoutParams(params);
        GlideApp.with(binding.ivImage.getContext())
                .load(bean.getImagePath())
                .error(R.drawable.def_small)
                .into(binding.ivImage);
        binding.ivBorder.setVisibility(selection == position ? View.VISIBLE:View.GONE);
    }
}
