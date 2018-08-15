package com.king.app.coolg.phone.star;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarOrdersBinding;
import com.king.app.gdb.data.entity.FavorStarOrder;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarOrdersAdapter extends BaseBindingAdapter<AdapterStarOrdersBinding, FavorStarOrder> {
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
    }
}
