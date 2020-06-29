package com.king.app.coolg.phone.star.list;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterStarRichBinding;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:20
 */
public class StarRichAdapter extends BaseBindingAdapter<AdapterStarRichBinding, StarProxy> {

    private Map<Long, Boolean> mExpandMap;

    private RequestOptions requestOptions;

    private OnStarRatingListener onStarRatingListener;

    public StarRichAdapter() {
        requestOptions = GlideUtil.getStarOptions();
    }

    public void setExpandMap(Map<Long, Boolean> mExpandMap) {
        this.mExpandMap = mExpandMap;
    }

    public void setOnStarRatingListener(OnStarRatingListener onStarRatingListener) {
        this.onStarRatingListener = onStarRatingListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_star_rich;
    }

    @Override
    protected void onBindItem(AdapterStarRichBinding binding, int position, StarProxy bean) {
        Star star = bean.getStar();
        binding.tvName.setText(star.getName());
        binding.tvVideos.setText(star.getRecords() + " Videos");
        binding.tvType.setText(getTypeText(star));
        updateScore(binding.tvScore, star);
        updateScoreC(binding.tvScoreC, star);

        binding.tvIndex.setText(String.valueOf(position + 1));

        if (star.getRatings().size() > 0) {
            StarRating rating = star.getRatings().get(0);

            binding.tvRating.setText(StarRatingUtil.getRatingValue(rating.getComplex()));
            StarRatingUtil.updateRatingColor(binding.tvRating, rating);

            binding.tvFace.setText("Face " + StarRatingUtil.getSubRatingValue(rating.getFace()));
            binding.tvFace.setTextColor(StarRatingUtil.getSubRatingColor(rating.getFace(), binding.tvFace.getResources()));
            binding.tvBody.setText("Body " + StarRatingUtil.getSubRatingValue(rating.getBody()));
            binding.tvBody.setTextColor(StarRatingUtil.getSubRatingColor(rating.getBody(), binding.tvFace.getResources()));
            binding.tvSex.setText("Sexuality " + StarRatingUtil.getSubRatingValue(rating.getSexuality()));
            binding.tvSex.setTextColor(StarRatingUtil.getSubRatingColor(rating.getSexuality(), binding.tvFace.getResources()));
            binding.tvDk.setText("Dk/Butt " + StarRatingUtil.getSubRatingValue(rating.getDk()));
            binding.tvDk.setTextColor(StarRatingUtil.getSubRatingColor(rating.getDk(), binding.tvFace.getResources()));
            binding.tvPassion.setText("Passion " + StarRatingUtil.getSubRatingValue(rating.getPassion()));
            binding.tvPassion.setTextColor(StarRatingUtil.getSubRatingColor(rating.getPassion(), binding.tvFace.getResources()));
            binding.tvVideo.setText("Video " + StarRatingUtil.getSubRatingValue(rating.getVideo()));
            binding.tvVideo.setTextColor(StarRatingUtil.getSubRatingColor(rating.getVideo(), binding.tvFace.getResources()));
            binding.tvPrefer.setText("Prefer " + StarRatingUtil.getSubRatingValue(rating.getPrefer()));
            binding.tvPrefer.setTextColor(StarRatingUtil.getSubRatingColor(rating.getPrefer(), binding.tvPrefer.getResources()));
            binding.groupRating.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvRating.setText(StarRatingUtil.NON_RATING);
            StarRatingUtil.updateRatingColor(binding.tvRating, null);
            binding.groupRating.setVisibility(View.GONE);
        }
        binding.tvRating.setTag(position);
        binding.tvRating.setOnClickListener(ratingListener);

        Glide.with(binding.ivPlayer.getContext())
                .load(list.get(position).getImagePath())
                .apply(requestOptions)
                .into(binding.ivPlayer);

        binding.groupExpand.setVisibility(mExpandMap.get(star.getId()) ? View.VISIBLE:View.GONE);
        binding.ivMore.setImageResource(mExpandMap.get(star.getId()) ? R.drawable.ic_keyboard_arrow_up_666_24dp:R.drawable.ic_keyboard_arrow_down_666_24dp);
        binding.ivMore.setTag(position);
        binding.ivMore.setOnClickListener(moreListener);

    }

    private View.OnClickListener moreListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            long starId = list.get(position).getStar().getId();
            boolean targetExpand = !mExpandMap.get(starId);
            mExpandMap.put(starId, targetExpand);
            notifyItemChanged(position);
        }
    };

    private View.OnClickListener ratingListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag();
            if (onStarRatingListener != null) {
                onStarRatingListener.onUpdateRating(position, list.get(position).getStar().getId());
            }
        }
    };

    private void updateScore(TextView view, Star star) {
        StringBuffer buffer = new StringBuffer();
        if (star.getMax() > 0) {
            if (star.getMin() == star.getMax()) {
                buffer.append("score(").append(star.getMax()).append(")");
            }
            else {
                buffer.append("max(").append(star.getMax()).append(")  ")
                        .append("min(").append(star.getMin()).append(")  ")
                        .append("avg(").append(FormatUtil.formatScore(star.getAverage(), 1)).append(")");
            }
            view.setText(buffer.toString());
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private void updateScoreC(TextView view, Star star) {
        StringBuffer buffer = new StringBuffer();
        if (star.getCmax() > 0) {
            if (star.getCmax() == star.getCmin()) {
                buffer.append("C score(").append(star.getCmax()).append(")");
            }
            else {
                buffer.append("C max(").append(star.getCmax()).append(")  ")
                        .append("min(").append(star.getCmin()).append(")  ")
                        .append("avg(").append(FormatUtil.formatScore(star.getCaverage(), 1)).append(")");
            }
            view.setText(buffer.toString());
            view.setVisibility(View.VISIBLE);
        }
        else {
            view.setVisibility(View.GONE);
        }
    }

    private String getTypeText(Star star) {
        String text = "";
        if (star.getBetop() > 0) {
            text = "Top " + star.getBetop();
        }
        if (star.getBebottom() > 0) {
            if (!TextUtils.isEmpty(text)) {
                text = text + ", ";
            }
            text = text + "Bottom " + star.getBebottom();
        }
        return text;
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
