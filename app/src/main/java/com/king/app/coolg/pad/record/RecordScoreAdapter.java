package com.king.app.coolg.pad.record;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordScorePadBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:35
 */
public class RecordScoreAdapter extends BaseBindingAdapter<AdapterRecordScorePadBinding, TitleValueBean> {
    
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_score_pad;
    }

    @Override
    protected void onBindItem(AdapterRecordScorePadBinding binding, int position, TitleValueBean bean) {
        if (bean.isOnlyValue()) {
            binding.tvTitle.setVisibility(View.GONE);
        }
        else {
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvTitle.setText(bean.getTitle());
        }
        binding.tvValue.setText(bean.getValue());
    }
}
