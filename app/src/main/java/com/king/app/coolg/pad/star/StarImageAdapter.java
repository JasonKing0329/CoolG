package com.king.app.coolg.pad.star;

import com.bumptech.glide.Glide;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarImagePadBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/20 16:24
 */
public class StarImageAdapter extends BaseBindingAdapter<AdapterStarImagePadBinding, String> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_image_pad;
    }

    @Override
    protected void onBindItem(AdapterStarImagePadBinding binding, int position, String path) {
        if (path != null && path.toLowerCase().endsWith(".gif")) {
            GlideApp.with(binding.ivRecord.getContext())
                    .asGif()
                    .load(path)
                    .error(R.drawable.def_small)
                    .into(binding.ivRecord);
        }
        else {
            GlideApp.with(binding.ivRecord.getContext())
                    .load(path)
                    .error(R.drawable.def_small)
                    .into(binding.ivRecord);
        }
    }
}
