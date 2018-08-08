package com.king.app.coolg.phone.home;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.AdapterRecommendFilterBinding;
import com.king.app.coolg.model.bean.RecordFilterBean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 10:15
 */
public class RecommendFilterAdapter extends BaseBindingAdapter<AdapterRecommendFilterBinding, RecordFilterBean> {
    
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_recommend_filter;
    }

    @Override
    protected void onClickItem(View v, int position) {
        RecordFilterBean bean = list.get(position);
        boolean checked = bean.isEnable();
        checked = !checked;
        bean.setEnable(checked);
        if (!checked) {
            bean.setMax(0);
            bean.setMin(0);
        }
        notifyItemChanged(position);
    }

    @Override
    protected void onBindItem(AdapterRecommendFilterBinding binding, int position, RecordFilterBean bean) {
        binding.cbCheck.setText(bean.getKeyword());
        binding.cbCheck.setChecked(bean.isEnable());

        binding.llInputGroup.setVisibility(bean.isEnable() ? View.VISIBLE:View.INVISIBLE);

        bindInputMin(binding, bean);
        bindInputMax(binding, bean);
    }

    private void bindInputMax(AdapterRecommendFilterBinding binding, RecordFilterBean bean) {
        if (binding.etInputMax.getTag() instanceof TextWatcher) {
            binding.etInputMax.removeTextChangedListener((TextWatcher) binding.etInputMax.getTag());
        }
        if (bean.getMax() > 0) {
            binding.etInputMax.setText(String.valueOf(bean.getMax()));
        }
        else {
            if (bean.getKeyword().equals(AppConstants.FILTER_KEY_SCORE_DEPRECATED)) {
                binding.etInputMax.setText("0");
            }
            else {
                binding.etInputMax.setText("");
            }
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    bean.setMax(0);
                }
                else {
                    bean.setMax(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.etInputMax.setTag(watcher);
        binding.etInputMax.addTextChangedListener(watcher);
    }

    private void bindInputMin(AdapterRecommendFilterBinding binding, RecordFilterBean bean) {
        if (binding.etInputMin.getTag() instanceof TextWatcher) {
            binding.etInputMin.removeTextChangedListener((TextWatcher) binding.etInputMin.getTag());
        }
        if (bean.getMin() > 0) {
            binding.etInputMin.setText(String.valueOf(bean.getMin()));
        }
        else {
            if (bean.getKeyword().equals(AppConstants.FILTER_KEY_SCORE_DEPRECATED)) {
                binding.etInputMin.setText("0");
            }
            else {
                binding.etInputMin.setText("");
            }
        }
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    bean.setMin(0);
                }
                else {
                    bean.setMin(Integer.parseInt(s.toString()));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        binding.etInputMin.setTag(watcher);
        binding.etInputMin.addTextChangedListener(watcher);
    }
}
