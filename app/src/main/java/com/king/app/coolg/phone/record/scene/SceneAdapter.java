package com.king.app.coolg.phone.record.scene;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterSceneNameBinding;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.RippleUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/13 9:13
 */
public class SceneAdapter extends BaseBindingAdapter<AdapterSceneNameBinding, SceneBean> {

    private String mFocusScene;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_scene_name;
    }

    @Override
    protected void onBindItem(AdapterSceneNameBinding binding, int position, SceneBean bean) {
        binding.tvName.setText(bean.getScene());

        if (bean.getScene().equals(mFocusScene)) {
            binding.groupContainer.setBackground(getBackground(binding.groupContainer.getResources().getColor(R.color.colorAccent)));
        }
        else {
            binding.groupContainer.setBackground(getBackground(bean.getColor()));
        }

        binding.tvNumber.setText(String.valueOf(bean.getNumber()));
        binding.tvAvg.setText("Avg(" + FormatUtil.formatFloatEnd(bean.getAverage()) + ")");
        binding.tvMax.setText("Max(" + bean.getMax() + ")");
    }

    private Drawable getBackground(int color) {
        return RippleUtil.getRippleBackground(color, Color.argb(0x66, 0, 0, 0));
    }

    public void setFocusScene(String scene) {
        mFocusScene = scene;
    }
}
