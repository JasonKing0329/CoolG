package com.king.app.coolg.phone.star;

import android.view.View;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.base.adapter.BaseRecyclerAdapter;
import com.king.app.coolg.databinding.AdapterStarOrdersBinding;
import com.king.app.gdb.data.entity.FavorStarOrder;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarOrdersAdapter extends BaseBindingAdapter<AdapterStarOrdersBinding, FavorStarOrder> {

    private boolean deleteMode;

    private OnDeleteListener onDeleteListener;

    public StarOrdersAdapter() {
        setOnItemLongClickListener((view, position, data) -> {
            toggleDeleteMode();
            notifyDataSetChanged();
        });
    }

    public void toggleDeleteMode() {
        deleteMode = !deleteMode;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_orders;
    }

    @Override
    protected void onBindItem(AdapterStarOrdersBinding binding, int position, FavorStarOrder bean) {
        binding.tvName.setText(bean.getName());
        GlideApp.with(binding.ivHead.getContext())
                .load(bean.getCoverUrl())
                .error(R.drawable.def_small)
                .into(binding.ivHead);

        if (deleteMode) {
            binding.ivDelete.setVisibility(View.VISIBLE);
            binding.ivDelete.setOnClickListener(v -> {
                if (onDeleteListener != null) {
                    onDeleteListener.onDeleteOrder(bean);
                }
            });
        }
        else {
            binding.ivDelete.setVisibility(View.GONE);
            binding.ivDelete.setOnClickListener(null);
        }
    }

    public interface OnDeleteListener {
        void onDeleteOrder(FavorStarOrder order);
    }
}
