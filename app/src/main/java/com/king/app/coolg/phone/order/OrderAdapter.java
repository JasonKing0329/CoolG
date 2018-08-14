package com.king.app.coolg.phone.order;

import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterOrderPhoneBinding;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 11:50
 */
public class OrderAdapter<T> extends BaseBindingAdapter<AdapterOrderPhoneBinding, OrderItem<T>> {

    private boolean selectionMode;

    private Map<Long, Boolean> mCheckMap;

    private OnEditListener<T> onEditListener;

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (!selectionMode) {
            mCheckMap.clear();
        }
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    public void setOnEditListener(OnEditListener<T> onEditListener) {
        this.onEditListener = onEditListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_order_phone;
    }

    @Override
    protected void onBindItem(AdapterOrderPhoneBinding binding, int position, OrderItem<T> bean) {
        binding.tvName.setText(bean.getName());
        binding.tvNumber.setText(String.valueOf(bean.getNumber()));
        GlideApp.with(binding.ivCover.getContext())
                .load(bean.getImagePath())
                .error(R.drawable.def_small)
                .into(binding.ivCover);
        binding.cbCheck.setVisibility(selectionMode ? View.VISIBLE:View.GONE);
        binding.cbCheck.setChecked(mCheckMap.get(bean.getId()) == null ? false:true);
        binding.ivEdit.setOnClickListener(v -> {
            if (onEditListener != null) {
                onEditListener.onEdit(v, position, bean);
            }
        });
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selectionMode) {
            if (mCheckMap.get(list.get(position).getId()) == null) {
                mCheckMap.put(list.get(position).getId(), true);
            }
            else {
                mCheckMap.remove(list.get(position).getId());
            }
            notifyItemChanged(position);
        }
        else {
            super.onClickItem(v, position);
        }
    }

    public interface OnEditListener<T> {
        void onEdit(View view, int position, OrderItem<T> data);
    }
}
