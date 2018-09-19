package com.king.app.coolg.phone.studio;

import android.graphics.drawable.GradientDrawable;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStudioSimpleBinding;
import com.king.app.coolg.utils.ColorUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 11:06
 */
public class StudioSimpleAdapter extends BaseBindingAdapter<AdapterStudioSimpleBinding, StudioSimpleItem> {

    private Map<String, Integer> colorMap;

    public StudioSimpleAdapter() {
        createCharColors();
    }

    private void createCharColors() {
        colorMap = new HashMap<>();
        for (char i = 'A'; i <= 'Z'; i ++) {
            colorMap.put(String.valueOf(i), ColorUtil.randomWhiteTextBgColor());
        }
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_studio_simple;
    }

    @Override
    protected void onBindItem(AdapterStudioSimpleBinding binding, int position, StudioSimpleItem bean) {
        binding.setBean(bean);
        binding.tvIndex.setText(String.valueOf(position + 1));

        Integer color = colorMap.get(bean.getFirstChar());
        if (color != null) {
            GradientDrawable drawable = (GradientDrawable) binding.tvChar.getBackground();
            drawable.setColor(color);
            binding.tvChar.setBackground(drawable);
        }
    }
}
