package com.king.app.coolg.phone.star;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
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

    private StarRatingViewModel mRatingModel;

    private StarViewModel mModel;

    private AdapterStarPhoneHeaderBinding mBinding;

    private StarRelationshipAdapter relationshipAdapter;

    private StarOrdersAdapter ordersAdapter;

    private OnHeadActionListener onHeadActionListener;

    public StarHeader() {
        starOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void bind(AdapterStarPhoneHeaderBinding binding, Star star, List<String> starImageList, int recordNumber, List<StarRelationship> relationships) {

        mBinding = binding;

        binding.starFace.setOnStarChangeListener(this);
        binding.starBody.setOnStarChangeListener(this);
        binding.starDk.setOnStarChangeListener(this);
        binding.starPassion.setOnStarChangeListener(this);
        binding.starVideo.setOnStarChangeListener(this);
        binding.starSex.setOnStarChangeListener(this);

        bindBasicInfo(binding, star, recordNumber);
        bindImages(binding, starImageList);
        bindRelationships(binding, relationships);
        bindRatings(binding, star);
        bindOrders(binding, star);
    }

    private void bindOrders(AdapterStarPhoneHeaderBinding binding, Star star) {
        binding.ivOrderAdd.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.addStarToOrder(star);
            }
        });
        binding.groupOrder.setOnClickListener(view -> {
            // collapse
            if (binding.ivOrderArrow.isSelected()) {
                binding.ivOrderArrow.setSelected(false);
                binding.ivOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);
                binding.rvOrders.setVisibility(View.GONE);
            }
            // expand
            else {
                binding.ivOrderArrow.setSelected(true);
                binding.ivOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_700_24dp);
                binding.rvOrders.setVisibility(View.VISIBLE);
            }
        });
        binding.rvOrders.setLayoutManager(new LinearLayoutManager(binding.rvOrders.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (mModel == null) {
            mModel = ViewModelProviders.of((FragmentActivity) binding.banner.getContext()).get(StarViewModel.class);
            mModel.ordersObserver.observe((LifecycleOwner) binding.rvOrders.getContext(), list -> {
                mBinding.tvOrder.setText(String.valueOf(list.size()));
                // 添加order后用notifyDataSetChanged不知为何不管用，还得重新setAdapter才管用
                ordersAdapter = new StarOrdersAdapter();
                ordersAdapter.setList(list);
                mBinding.rvOrders.setAdapter(ordersAdapter);
            });
        }
        mModel.loadStarOrders(star);
    }

    private void bindRatings(AdapterStarPhoneHeaderBinding binding, Star star) {
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

        binding.tvRating.setText("");
        if (mRatingModel == null) {
            mRatingModel = ViewModelProviders.of((FragmentActivity) binding.banner.getContext()).get(StarRatingViewModel.class);
            mRatingModel.ratingObserver.observe((LifecycleOwner) binding.banner.getContext(), rating -> showRatings(binding, rating));
        }
        mRatingModel.loadStarRating(star.getId());
    }

    private void bindRelationships(AdapterStarPhoneHeaderBinding binding, List<StarRelationship> relationships) {
        binding.tvRelation.setText(relationships.size() + "人");
        binding.rvRelation.setLayoutManager(new LinearLayoutManager(binding.rvRelation.getContext(), LinearLayoutManager.HORIZONTAL, false));
        if (relationshipAdapter == null) {
            binding.rvRelation.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view);
                    if (position > 0) {
                        outRect.left = ScreenUtils.dp2px(10);
                    }
                }
            });
            relationshipAdapter = new StarRelationshipAdapter();
            relationshipAdapter.setList(relationships);
            relationshipAdapter.setOnItemClickListener((view, position, data) -> {
                if (onHeadActionListener != null) {
                    onHeadActionListener.onClickRelationStar(data);
                }
            });
            binding.rvRelation.setAdapter(relationshipAdapter);
        }
        else {
            relationshipAdapter.setList(relationships);
            relationshipAdapter.notifyDataSetChanged();
        }
        binding.groupRelation.setOnClickListener(view -> {
            // collapse
            if (binding.ivRelationArrow.isSelected()) {
                binding.ivRelationArrow.setSelected(false);
                binding.ivRelationArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);
                binding.rvRelation.setVisibility(View.GONE);
            }
            // expand
            else {
                binding.ivRelationArrow.setSelected(true);
                binding.ivRelationArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_700_24dp);
                binding.rvRelation.setVisibility(View.VISIBLE);
            }
        });
    }

    private void bindImages(AdapterStarPhoneHeaderBinding binding, List<String> starImageList) {
        if (ListUtil.isEmpty(starImageList) || starImageList.size() == 1) {
            binding.banner.setVisibility(View.INVISIBLE);
            binding.ivStar.setVisibility(View.VISIBLE);
            if (starImageList.size() == 1) {
                Glide.with(binding.ivStar.getContext())
                        .load(starImageList.get(0))
                        .apply(starOptions)
                        .into(binding.ivStar);
                binding.ivSetCover.setVisibility(View.VISIBLE);
                binding.ivSetCover.setOnClickListener(v -> {
                    if (onHeadActionListener != null) {
                        onHeadActionListener.onApplyImage(starImageList.get(0));
                    }
                });
            }
            else {
                binding.ivSetCover.setVisibility(View.INVISIBLE);
            }
        }
        else {
            binding.ivStar.setVisibility(View.INVISIBLE);
            binding.ivSetCover.setVisibility(View.INVISIBLE);
            binding.banner.setVisibility(View.VISIBLE);
            showBanner(binding.banner, starImageList);
        }
    }

    private void bindBasicInfo(AdapterStarPhoneHeaderBinding binding, Star star, int recordNumber) {
        StringBuffer buffer = new StringBuffer(recordNumber + "个视频文件");
        if (star.getBetop() > 0) {
            buffer.append("  ").append(star.getBetop()).append(" Top");
        }
        if (star.getBebottom() > 0) {
            buffer.append("  ").append(star.getBebottom()).append(" Bottom");
        }
        binding.tvVideos.setText(buffer.toString());
    }

    public void showRatings(AdapterStarPhoneHeaderBinding binding, StarRating rating) {

        binding.tvRating.setText(mRatingModel.getComplex());

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
                mRatingModel.getRating().setFace(checkedStar);
                mBinding.tvFace.setText(rateValue);
                break;
            case R.id.star_body:
                mRatingModel.getRating().setBody(checkedStar);
                mBinding.tvBody.setText(rateValue);
                break;
            case R.id.star_dk:
                mRatingModel.getRating().setDk(checkedStar);
                mBinding.tvDk.setText(rateValue);
                break;
            case R.id.star_passion:
                mRatingModel.getRating().setPassion(checkedStar);
                mBinding.tvPassion.setText(rateValue);
                break;
            case R.id.star_video:
                mRatingModel.getRating().setVideo(checkedStar);
                mBinding.tvVideo.setText(rateValue);
                break;
            case R.id.star_sex:
                mRatingModel.getRating().setSexuality(checkedStar);
                mBinding.tvSex.setText(rateValue);
                break;
        }
        mBinding.tvRating.setText(mRatingModel.getComplex());
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_image, null);
            ImageView imageView = view.findViewById(R.id.iv_image);
            ImageView ivCover = view.findViewById(R.id.iv_set_cover);
            ivCover.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(path)
                    .apply(starOptions)
                    .into(imageView);

            ivCover.setOnClickListener(v -> {
                if (onHeadActionListener != null) {
                    onHeadActionListener.onApplyImage(path);
                }
            });
            return view;
        }
    }

    public interface OnHeadActionListener {
        void onClickRelationStar(StarRelationship relationship);
        void onApplyImage(String path);

        void addStarToOrder(Star star);
    }

}
