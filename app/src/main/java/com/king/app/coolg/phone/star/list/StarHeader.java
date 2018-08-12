package com.king.app.coolg.phone.star.list;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.AdapterStarPhoneHeaderBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.StarRatingViewModel;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.coolg.view.widget.StarRatingView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/8/12 0012.
 */

public class StarHeader implements StarRatingView.OnStarChangeListener {

    private RequestOptions starOptions;

    private StarRatingViewModel mModel;

    private AdapterStarPhoneHeaderBinding mBinding;

    public StarHeader() {
        starOptions = GlideUtil.getStarWideOptions();
    }

    public void bind(AdapterStarPhoneHeaderBinding binding, Star star, List<String> starImageList, int recordNumber) {

        mBinding = binding;
        mModel = ViewModelProviders.of((FragmentActivity) binding.banner.getContext()).get(StarRatingViewModel.class);

        binding.tvVideos.setText(recordNumber + "个视频文件");

        if (ListUtil.isEmpty(starImageList) || starImageList.size() == 1) {
            binding.banner.setVisibility(View.INVISIBLE);
            binding.ivRecord.setVisibility(View.VISIBLE);
            if (starImageList.size() == 1) {
                Glide.with(binding.ivRecord.getContext())
                        .load(starImageList.get(0))
                        .apply(starOptions)
                        .into(binding.ivRecord);
            }
        }
        else {
            binding.ivRecord.setVisibility(View.INVISIBLE);
            binding.banner.setVisibility(View.VISIBLE);
            showBanner(binding.banner, starImageList);
        }
        
        binding.groupRating.setOnClickListener(view -> {
            // collapse
            if (binding.ivRatingArrow.isSelected()) {
                binding.ivRatingArrow.setSelected(false);
                binding.ivRatingArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);
                binding.groupRatingSub.setVisibility(View.GONE);
            }
            // expand
            else {
                binding.ivRatingArrow.setSelected(true);
                binding.ivRatingArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_700_24dp);
                binding.groupRatingSub.setVisibility(View.VISIBLE);
            }
        });

        binding.starFace.setOnStarChangeListener(this);
        binding.starBody.setOnStarChangeListener(this);
        binding.starDk.setOnStarChangeListener(this);
        binding.starPassion.setOnStarChangeListener(this);
        binding.starVideo.setOnStarChangeListener(this);
        binding.starSex.setOnStarChangeListener(this);

        mModel.ratingObserver.observe((LifecycleOwner) binding.banner.getContext(), rating -> showRatings(binding, rating));
        mModel.loadStarRating(star.getId());
    }

    public void showRatings(AdapterStarPhoneHeaderBinding binding, StarRating rating) {

        binding.tvRating.setText(mModel.getComplex());

        binding.starFace.setCheckNumber(rating.getFace());
        binding.tvFace.setText(StarRatingUtil.getRatingValue(rating.getFace()));
        binding.starBody.setCheckNumber(rating.getBody());
        binding.tvBody.setText(StarRatingUtil.getRatingValue(rating.getBody()));
        binding.starDk.setCheckNumber(rating.getDk());
        binding.tvDk.setText(StarRatingUtil.getRatingValue(rating.getDk()));
        binding.starSex.setCheckNumber(rating.getSexuality());
        binding.tvSex.setText(StarRatingUtil.getRatingValue(rating.getSexuality()));
        binding.starPassion.setCheckNumber(rating.getPassion());
        binding.tvPassion.setText(StarRatingUtil.getRatingValue(rating.getPassion()));
        binding.starVideo.setCheckNumber(rating.getVideo());
        binding.tvVideo.setText(StarRatingUtil.getRatingValue(rating.getVideo()));
    }

    private void showBanner(LMBanners banner, List<String> list) {
        setBannerParams(banner);
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        banner.setAdapter(adapter, list);
    }

    private void setBannerParams(LMBanners banner) {
        // 禁用btnStart(只在onPageScroll触发后有效)
        banner.isGuide(false);
        // 显示引导圆点
//        lmBanners.hideIndicatorLayout();
        banner.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);
        // 可以不写，因为文件名直接覆用的mBinding.banner-1.0.8里的res
        banner.setSelectIndicatorRes(R.drawable.page_indicator_select);
        banner.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect);
        // 轮播切换时间
        banner.setDurtion(SettingProperty.getStarRecommendAnimTime());
        if (SettingProperty.isStarRandomRecommend()) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(banner, type);
        } else {
            LMBannerViewUtil.setScrollAnim(banner, SettingProperty.getStarRecommendAnimType());
        }
    }

    @Override
    public void onStarChanged(StarRatingView view, float checkedStar) {
        String rateValue = StarRatingUtil.getRatingValue(checkedStar);
        switch (view.getId()) {
            case R.id.star_face:
                mModel.getRating().setFace(checkedStar);
                mBinding.tvFace.setText(rateValue);
                break;
            case R.id.star_body:
                mModel.getRating().setBody(checkedStar);
                mBinding.tvBody.setText(rateValue);
                break;
            case R.id.star_dk:
                mModel.getRating().setDk(checkedStar);
                mBinding.tvDk.setText(rateValue);
                break;
            case R.id.star_passion:
                mModel.getRating().setPassion(checkedStar);
                mBinding.tvPassion.setText(rateValue);
                break;
            case R.id.star_video:
                mModel.getRating().setVideo(checkedStar);
                mBinding.tvVideo.setText(rateValue);
                break;
            case R.id.star_sex:
                mModel.getRating().setSexuality(checkedStar);
                mBinding.tvSex.setText(rateValue);
                break;
        }
        mBinding.tvRating.setText(mModel.getComplex());
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_image, null);
            ImageView imageView = view.findViewById(R.id.iv_image);

            Glide.with(context)
                    .load(path)
                    .apply(starOptions)
                    .into(imageView);
            return view;
        }
    }

}
