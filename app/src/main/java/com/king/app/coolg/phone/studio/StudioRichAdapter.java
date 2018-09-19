package com.king.app.coolg.phone.studio;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStudioRichBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 11:06
 */
public class StudioRichAdapter extends BaseBindingAdapter<AdapterStudioRichBinding, StudioRichItem> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_studio_rich;
    }

    @Override
    protected void onBindItem(AdapterStudioRichBinding binding, int position, StudioRichItem bean) {
        binding.setBean(bean);
        binding.tvIndex.setText(String.valueOf(position + 1));
        GlideApp.with(binding.ivImage.getContext())
                .load(bean.getImageUrl())
                .error(R.drawable.def_small)
                .into(binding.ivImage);
    }
}
