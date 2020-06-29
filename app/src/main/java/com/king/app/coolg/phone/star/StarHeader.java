package com.king.app.coolg.phone.star;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseTagAdapter;
import com.king.app.coolg.databinding.AdapterStarPhoneHeaderBinding;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.app.coolg.view.widget.StarRatingView;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.Tag;
import com.king.lib.banner.CoolBanner;
import com.king.lib.banner.CoolBannerAdapter;
import com.king.lib.banner.guide.GuideView;

import java.util.ArrayList;
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

    private StarStudioTag selectedTag;

    private Integer mTotalRecordNumber;

    private TagAdapter tagAdapter;

    public StarHeader() {
        starOptions = GlideUtil.getStarWideOptions();
    }

    public void setOnHeadActionListener(OnHeadActionListener onHeadActionListener) {
        this.onHeadActionListener = onHeadActionListener;
    }

    public void bind(AdapterStarPhoneHeaderBinding binding, Star star, List<String> starImageList
            , int recordNumber, List<StarRelationship> relationships, List<StarStudioTag> studioList, List<Tag> tagList) {

        mBinding = binding;

        binding.starFace.setOnStarChangeListener(this);
        binding.starBody.setOnStarChangeListener(this);
        binding.starDk.setOnStarChangeListener(this);
        binding.starPassion.setOnStarChangeListener(this);
        binding.starVideo.setOnStarChangeListener(this);
        binding.starSex.setOnStarChangeListener(this);
        binding.starPrefer.setOnStarChangeListener(this);

        bindBasicInfo(binding, star, recordNumber);
        bindImages(binding, starImageList);
        bindRelationships(binding, relationships);
        bindRatings(binding, star);
        bindOrders(binding, star);
        bindStudios(binding, studioList);
        bindTags(binding, tagList);
    }

    private void bindTags(AdapterStarPhoneHeaderBinding binding, List<Tag> tagList) {
        if (ListUtil.isEmpty(tagList)) {
            binding.tvTagsTitle.setVisibility(View.VISIBLE);
            binding.ivTagDelete.setVisibility(View.GONE);
            binding.rvTags.setVisibility(View.GONE);
        }
        else {
            binding.tvTagsTitle.setVisibility(View.GONE);
            binding.ivTagDelete.setVisibility(View.VISIBLE);
            binding.rvTags.setVisibility(View.VISIBLE);
            binding.ivTagDelete.setOnClickListener(v -> {
                if (tagAdapter != null) {
                    tagAdapter.toggleDelete();
                    tagAdapter.notifyDataSetChanged();
                }
            });
            if (tagAdapter == null) {
                binding.rvTags.setLayoutManager(new LinearLayoutManager(binding.rvTags.getContext(), LinearLayoutManager.HORIZONTAL, false));
                binding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                        int position = parent.getChildLayoutPosition(view);
                        if (position > 0) {
                            outRect.left = ScreenUtils.dp2px(10);
                        }
                    }
                });
                tagAdapter = new TagAdapter();
                tagAdapter.setOnDeleteListener((position, bean) -> onHeadActionListener.onDeleteTag(bean));
                tagAdapter.setOnItemLongClickListener((view, position, data) -> {
                    tagAdapter.toggleDelete();
                    tagAdapter.notifyDataSetChanged();
                });
                tagAdapter.setList(tagList);
                binding.rvTags.setAdapter(tagAdapter);
            }
            else {
                tagAdapter.setList(tagList);
                tagAdapter.notifyDataSetChanged();
            }
        }
        binding.ivTagAdd.setOnClickListener(v -> onHeadActionListener.onAddTag());
    }

    private void bindStudios(AdapterStarPhoneHeaderBinding binding, List<StarStudioTag> studioList) {
        if (studioList.size() > 0) {
            if (studioList.size() == 1) {
                binding.tvStudioSingle.setText(studioList.get(0).getName());
                binding.tvStudioSingle.setVisibility(View.VISIBLE);
                binding.flowStudios.setVisibility(View.GONE);
            }
            else {
                binding.tvStudioSingle.setVisibility(View.GONE);

                binding.flowStudios.removeAllViews();
                BaseTagAdapter<StarStudioTag> adapter = new BaseTagAdapter<StarStudioTag>() {
                    @Override
                    protected String getText(StarStudioTag data) {
                        return data.getName() + "(" + data.getCount() + ")";
                    }

                    @Override
                    protected long getId(StarStudioTag data) {
                        return data.getStudioId();
                    }

                    @Override
                    protected boolean isDisabled(StarStudioTag item) {
                        return false;
                    }
                };
                adapter.enableUnselect();
                adapter.setData(studioList);
                if (selectedTag != null) {
                    List<StarStudioTag> tags = new ArrayList<>();
                    tags.add(selectedTag);
                    adapter.setSelectedList(tags);
                }
                adapter.setOnItemSelectListener(new BaseTagAdapter.OnItemSelectListener<StarStudioTag>() {
                    @Override
                    public void onSelectItem(StarStudioTag tag) {
                        selectedTag = tag;
                        if (onHeadActionListener != null) {
                            onHeadActionListener.onFilterStudio(tag.getStudioId());
                        }
                    }

                    @Override
                    public void onUnSelectItem(StarStudioTag tag) {
                        selectedTag = null;
                        if (onHeadActionListener != null) {
                            onHeadActionListener.onCancelFilterStudio(tag.getStudioId());
                        }
                    }
                });
                adapter.bindFlowLayout(binding.flowStudios);
                binding.flowStudios.setVisibility(View.VISIBLE);
            }
            binding.flowStudios.setVisibility(View.VISIBLE);
        }
        else {
            binding.tvStudioSingle.setVisibility(View.GONE);
            binding.flowStudios.setVisibility(View.GONE);
        }
    }

    private void bindOrders(AdapterStarPhoneHeaderBinding binding, Star star) {
        binding.ivOrderAdd.setOnClickListener(v -> {
            if (onHeadActionListener != null) {
                onHeadActionListener.addStarToOrder(star);
            }
        });
        binding.ivOrderDelete.setOnClickListener(v -> {
            if (ordersAdapter != null) {
                ordersAdapter.toggleDeleteMode();
                ordersAdapter.notifyDataSetChanged();
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
                ordersAdapter.setOnDeleteListener(order -> {
                    mModel.deleteOrderOfStar(order.getId(), star.getId());
                    mModel.loadStarOrders(star.getId());
                });
                ordersAdapter.setList(list);
                mBinding.rvOrders.setAdapter(ordersAdapter);
            });
        }
        mModel.loadStarOrders(star.getId());
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
            binding.guideView.setVisibility(View.INVISIBLE);
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
                binding.ivDelete.setVisibility(View.VISIBLE);
                binding.ivDelete.setOnClickListener(v -> {
                    if (onHeadActionListener != null) {
                        onHeadActionListener.onDeleteImage(starImageList.get(0));
                    }
                });
            }
            else {
                binding.ivDelete.setVisibility(View.INVISIBLE);
                binding.ivSetCover.setVisibility(View.INVISIBLE);
            }
        }
        else {
            binding.ivStar.setVisibility(View.INVISIBLE);
            binding.ivDelete.setVisibility(View.INVISIBLE);
            binding.ivSetCover.setVisibility(View.INVISIBLE);
            binding.banner.setVisibility(View.VISIBLE);
            binding.guideView.setVisibility(View.VISIBLE);
            showBanner(binding.banner, binding.guideView, starImageList);
        }
    }

    private void bindBasicInfo(AdapterStarPhoneHeaderBinding binding, Star star, int recordNumber) {
        // 页面第一次进入肯定是加载全部，后面可能会筛选list，因此以第一次加载的数量为准
        if (mTotalRecordNumber == null) {
            mTotalRecordNumber = recordNumber;
        }
        StringBuffer buffer = new StringBuffer(mTotalRecordNumber + "个视频文件");
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
        binding.tvFace.setText(StarRatingUtil.getSubRatingValue(rating.getFace()));
        binding.starBody.setCheckNumber(rating.getBody());
        binding.tvBody.setText(StarRatingUtil.getSubRatingValue(rating.getBody()));
        binding.starDk.setCheckNumber(rating.getDk());
        binding.tvDk.setText(StarRatingUtil.getSubRatingValue(rating.getDk()));
        binding.starSex.setCheckNumber(rating.getSexuality());
        binding.tvSex.setText(StarRatingUtil.getSubRatingValue(rating.getSexuality()));
        binding.starPassion.setCheckNumber(rating.getPassion());
        binding.tvPassion.setText(StarRatingUtil.getSubRatingValue(rating.getPassion()));
        binding.starVideo.setCheckNumber(rating.getVideo());
        binding.tvVideo.setText(StarRatingUtil.getSubRatingValue(rating.getVideo()));
        binding.starPrefer.setCheckNumber(rating.getPrefer());
        binding.tvPrefer.setText(StarRatingUtil.getSubRatingValue(rating.getPrefer()));
    }

    private void showBanner(CoolBanner banner, GuideView guideView, List<String> list) {
        setBannerParams(banner);
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        adapter.setList(list);

        guideView.setPointNumber(list.size());
        guideView.setGuideTextGravity(Gravity.CENTER);
        banner.setOnBannerPageListener((page, adapterIndex) -> guideView.setFocusIndex(page));

        banner.setAdapter(adapter);
        banner.startAutoPlay();
    }

    private void setBannerParams(CoolBanner banner) {
        BannerHelper.setBannerParams(banner, ViewProperty.getStarBannerParams());
    }

    @Override
    public void onStarChanged(StarRatingView view, float checkedStar) {
        String rateValue = StarRatingUtil.getSubRatingValue(checkedStar);
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
            case R.id.star_prefer:
                mRatingModel.getRating().setPrefer(checkedStar);
                mBinding.tvPrefer.setText(rateValue);
                break;
        }
        mBinding.tvRating.setText(mRatingModel.getComplex());
        mRatingModel.saveRating();
    }

    private class HeadBannerAdapter extends CoolBannerAdapter<String> {
        @Override
        protected int getLayoutRes() {
            return R.layout.adapter_banner_image;
        }

        @Override
        protected void onBindView(View view, int position, String path) {
            ImageView imageView = view.findViewById(R.id.iv_image);
            ImageView ivCover = view.findViewById(R.id.iv_set_cover);
            ImageView ivDelete = view.findViewById(R.id.iv_delete);
            ivCover.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.VISIBLE);

            Glide.with(view.getContext())
                    .load(path)
                    .apply(starOptions)
                    .into(imageView);

            ivCover.setOnClickListener(v -> {
                if (onHeadActionListener != null) {
                    onHeadActionListener.onApplyImage(path);
                }
            });
            ivDelete.setOnClickListener(v -> {
                if (onHeadActionListener != null) {
                    onHeadActionListener.onDeleteImage(path);
                }
            });
        }
    }

    public interface OnHeadActionListener {
        void onClickRelationStar(StarRelationship relationship);
        void onApplyImage(String path);
        void onDeleteImage(String path);
        void addStarToOrder(Star star);
        void onFilterStudio(long studioId);
        void onCancelFilterStudio(long studioId);
        void onAddTag();
        void onDeleteTag(Tag bean);
    }

}
