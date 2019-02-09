package com.king.app.coolg.phone.star.list;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarSelectorBinding;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.StarRating;

public class StarSelectorAdapter extends BaseBindingAdapter<AdapterStarSelectorBinding, StarProxy> {
    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_selector;
    }

    @Override
    protected void onBindItem(AdapterStarSelectorBinding binding, int position, StarProxy bean) {
        binding.setBean(bean);
        binding.tvIndex.setText(String.valueOf(position + 1));
        if (bean.getStar().getRatings().size() > 0) {
            StarRating rating = bean.getStar().getRatings().get(0);

            binding.tvRating.setText(StarRatingUtil.getRatingValue(rating.getComplex()));
            StarRatingUtil.updateRatingColor(binding.tvRating, rating);
        }
        else {
            binding.tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(binding.tvRating, null);
        }
    }

    @Override
    protected void onClickItem(View v, int position) {
        list.get(position).getObserver().onSelect(list.get(position));
    }
}
