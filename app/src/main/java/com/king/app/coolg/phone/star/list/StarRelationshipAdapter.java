package com.king.app.coolg.phone.star.list;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarRelationshipsBinding;
import com.king.app.coolg.phone.star.StarRelationship;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarRelationshipAdapter extends BaseBindingAdapter<AdapterStarRelationshipsBinding, StarRelationship> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_relationships;
    }

    @Override
    protected void onBindItem(AdapterStarRelationshipsBinding binding, int position, StarRelationship bean) {
        binding.tvName.setText(bean.getStar().getName());
        binding.tvCount.setText(bean.getCount() + "次");
        GlideApp.with(binding.ivHead.getContext())
                .load(bean.getImagePath())
                .error(R.drawable.def_person_square)
                .into(binding.ivHead);
    }
}
