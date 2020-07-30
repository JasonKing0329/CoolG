package com.king.app.coolg.phone.star.list;

import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarGridBinding;
import com.king.app.coolg.databinding.AdapterStarStaggerBinding;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.StarRatingUtil;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/30 10:01
 */
public class StarStaggerAdapter extends BaseBindingAdapter<AdapterStarStaggerBinding, StarProxy> {

    private OnStarRatingListener onStarRatingListener;

    private RequestOptions requestOptions;

    private boolean selectionMode;

    private Map<Long, Boolean> mCheckMap;

    public StarStaggerAdapter() {
        requestOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnStarRatingListener(OnStarRatingListener listener) {
        onStarRatingListener = listener;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        if (selectionMode) {
            mCheckMap.clear();
        }
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_stagger;
    }

    @Override
    protected void onBindItem(AdapterStarStaggerBinding binding, int position, StarProxy item) {

        // 瀑布流必须给item设置具体的宽高，否则会严重错位
        ViewGroup.LayoutParams params = binding.group.getLayoutParams();
        params.height = item.getHeight();
        params.width = item.getWidth();
        binding.group.setLayoutParams(params);
        params = binding.ivStar.getLayoutParams();
        params.height = item.getHeight();
        params.width = item.getWidth();
        binding.ivStar.setLayoutParams(params);

        binding.tvName.setText(item.getStar().getName());
        binding.tvVideos.setText(item.getStar().getRecords() + " Videos");
        binding.tvSeq.setText(String.valueOf(position + 1));

        String headPath = item.getImagePath();
        Glide.with(binding.ivStar.getContext())
                .load(headPath)
                .apply(requestOptions)
                .into(binding.ivStar);

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
                onStarRatingListener.onUpdateRating(position, item.getStar().getId());
            }
        });

        if (selectionMode) {
            binding.cbCheck.setVisibility(View.VISIBLE);
            binding.cbCheck.setChecked(mCheckMap.get(item.getStar().getId()) == null ? false:true);
        }
        else {
            binding.cbCheck.setVisibility(View.GONE);
        }
    }

    public void notifyStarChanged(Long starId) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (list.get(i).getStar().getId() == starId) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selectionMode) {
            long key = list.get(position).getStar().getId();
            if (mCheckMap.get(key) == null) {
                mCheckMap.put(key, true);
            }
            else {
                mCheckMap.remove(key);
            }
            notifyItemChanged(position);
        }
        else {
            super.onClickItem(v, position);
        }
    }
}
