package com.king.app.coolg.phone.star.list;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarCircleBinding;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.StarRatingUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:01
 */
public class StarCircleAdapter extends BaseBindingAdapter<AdapterStarCircleBinding, StarProxy> {
    
    private OnStarRatingListener onStarRatingListener;

    private RequestOptions requestOptions;

    public StarCircleAdapter() {
        requestOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnStarRatingListener(OnStarRatingListener listener) {
        onStarRatingListener = listener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_circle;
    }

    @Override
    protected void onBindItem(AdapterStarCircleBinding binding, int position, StarProxy item) {

        binding.tvName.setText(item.getStar().getName() + " (" + item.getStar().getRecords() + ")");

        String headPath = item.getImagePath();
        binding.ivHead.setVisibility(View.VISIBLE);
        Glide.with(binding.ivHead.getContext())
                .load(headPath)
                .apply(requestOptions)
                .into(binding.ivHead);

        if (ListUtil.isEmpty(item.getStar().getRatings())) {
            binding.tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(binding.tvRating, null);
        }
        else {
            binding.tvRating.setText(StarRatingUtil.getRatingValue(item.getStar().getRatings().get(0).getComplex()));
            StarRatingUtil.updateRatingColor(binding.tvRating, item.getStar().getRatings().get(0));
        }
        binding.tvRating.setTag(item);
        binding.tvRating.setOnClickListener(v -> {
            if (onStarRatingListener != null) {
                onStarRatingListener.onUpdateRating(item.getStar().getId());
            }
        });
    }

    public void notifyStarChanged(Long starId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getStar().getId() == starId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

}
