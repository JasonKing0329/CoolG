package com.king.app.coolg.phone.record.scene;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterSceneVerBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/8/30 14:31
 */
public class SceneVerAdapter extends BaseBindingAdapter<AdapterSceneVerBinding, SceneBean> {

    private int selection = 0;

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_scene_ver;
    }

    @Override
    protected void onBindItem(AdapterSceneVerBinding binding, int position, SceneBean bean) {
        binding.tvName.setText(bean.getScene());
        binding.tvNumber.setText(String.valueOf(bean.getNumber()));
        binding.tvName.setSelected(selection == position);
        binding.tvNumber.setSelected(selection == position);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (position != selection) {
            selection = position;
            super.onClickItem(v, position);
        }
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }
}
