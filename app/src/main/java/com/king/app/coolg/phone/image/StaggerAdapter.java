package com.king.app.coolg.phone.image;

import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterImageItemStaggerBinding;
import com.king.app.coolg.databinding.AdapterStarImagePadBinding;
import com.king.app.coolg.pad.star.StarImageBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/04 10:04
 */
public class StaggerAdapter extends AbsImageAdapter<AdapterImageItemStaggerBinding> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_image_item_stagger;
    }

    @Override
    protected void onBindItem(AdapterImageItemStaggerBinding binding, int position, ImageBean bean) {
        binding.setBean(bean);

        // 瀑布流必须给item设置具体的宽高，否则会严重错位
        ViewGroup.LayoutParams params = binding.group.getLayoutParams();
        params.height = bean.getHeight();
        params.width = bean.getWidth();
        binding.group.setLayoutParams(params);
        params = binding.ivImage.getLayoutParams();
        params.height = bean.getHeight();
        params.width = bean.getWidth();
        binding.ivImage.setLayoutParams(params);

        setCheckVisibility(binding.cbCheck);
        setImage(binding.ivImage, bean);
    }
}
