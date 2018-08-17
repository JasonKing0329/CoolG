package com.king.app.coolg.pad.home;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterHomeStarPadBinding;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 10:27
 */
public class HomeStarAdapter extends BaseBindingAdapter<AdapterHomeStarPadBinding, StarProxy> {

    private RequestOptions starOptions;

    public HomeStarAdapter() {
        starOptions = GlideUtil.getStarOptions();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_home_star_pad;
    }

    @Override
    protected void onBindItem(AdapterHomeStarPadBinding binding, int position, StarProxy bean) {

        Glide.with(binding.ivThumb.getContext())
                .load(list.get(position).getImagePath())
                .apply(starOptions)
                .into(binding.ivThumb);

        if (ScreenUtils.isTablet()) {
            binding.groupName.setVisibility(View.VISIBLE);
            binding.tvName.setText(list.get(position).getStar().getName());
            List<StarRating> ratings = list.get(position).getStar().getRatings();
            if (ListUtil.isEmpty(ratings)) {
                binding.tvRating.setText(StarRatingUtil.NON_RATING);
            }
            else {
                binding.tvRating.setText(StarRatingUtil.getRatingValue(ratings.get(0).getComplex()));
            }
        }
        else {
            binding.groupName.setVisibility(View.GONE);
        }
    }
}
