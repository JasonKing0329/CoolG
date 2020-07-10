package com.king.app.coolg.pad.star;

import android.view.ViewGroup;

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
public class StarImageAdapter extends BaseBindingAdapter<AdapterStarImagePadBinding, StarImageBean> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_image_pad;
    }

    @Override
    protected void onBindItem(AdapterStarImagePadBinding binding, int position, StarImageBean bean) {
        // 瀑布流必须给item设置具体的宽高，否则会严重错位
        ViewGroup.LayoutParams params = binding.ivRecord.getLayoutParams();
        params.height = bean.getHeight();
        params.width = bean.getWidth();
        binding.ivRecord.setLayoutParams(params);
        GlideApp.with(binding.ivRecord.getContext())
                .load(bean.getPath())
                .error(R.drawable.def_small)
                .into(binding.ivRecord);
    }
}
