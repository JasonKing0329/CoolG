package com.king.app.coolg.phone.record;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordScoreItemBinding;
import com.king.app.coolg.model.bean.TitleValueBean;

public class ScoreItemAdapter extends BaseBindingAdapter<AdapterRecordScoreItemBinding, TitleValueBean> {

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_score_item;
    }

    @Override
    protected void onBindItem(AdapterRecordScoreItemBinding binding, int position, TitleValueBean bean) {
        binding.setBean(bean);
        binding.divider.setVisibility(position == 0 ? View.GONE:View.VISIBLE);
    }
}
