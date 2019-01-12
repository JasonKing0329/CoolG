package com.king.app.coolg.phone.home;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterHomePlayListBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.gdb.data.entity.PlayItem;

public class HomePlayAdapter extends BaseBindingAdapter<AdapterHomePlayListBinding, PlayItem> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_play_list;
    }

    @Override
    protected void onBindItem(AdapterHomePlayListBinding binding, int position, PlayItem bean) {
        String url = null;
        try {
            url = ImageProvider.getRecordRandomPath(bean.getRecord().getName(), null);
        } catch (Exception e) {}

        GlideApp.with(binding.ivImage.getContext())
                .load(url)
                .error(R.drawable.def_small)
                .into(binding.ivImage);
    }
}
