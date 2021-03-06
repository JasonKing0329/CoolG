package com.king.app.coolg.pad.record;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordPadBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.palette.PaletteUtil;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.pad.star.StarPadActivity;
import com.king.app.coolg.pad.studio.StudioPadActivity;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.record.PassionPoint;
import com.king.app.coolg.phone.record.RecordOrdersAdapter;
import com.king.app.coolg.phone.record.RecordPlayOrdersAdapter;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.phone.video.player.PlayerActivity;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.dialog.content.TagFragment;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;
import com.king.lib.banner.BannerFlipStyleProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:47
 */
@Route("RecordPad")
public class RecordPadActivity extends MvvmActivity<ActivityRecordPadBinding, RecordPadViewModel> {

    public static final String EXTRA_RECORD_ID = "key_record_id";
    protected final int REQUEST_ADD_ORDER = 1602;
    protected final int REQUEST_SELECT_STUDIO = 1603;
    private final int REQUEST_VIDEO_ORDER = 1604;
    private final int REQUEST_SET_VIDEO_COVER = 1605;

    private RecordStarAdapter starAdapter;
    private RecordStarDetailAdapter starDetailAdapter;
    private RecordScoreAdapter scoreDetailAdapter;
    private PassionPointAdapter passionAdapter;

    private RecordPagerAdapter pagerAdapter;
    private RecordGallery recordGallery;

    private RecordOrdersAdapter ordersAdapter;
    private RecordPlayOrdersAdapter playOrdersAdapter;

    private TagAdapter tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_pad;
    }

    @Override
    protected void initView() {
        ColorUtil.updateIconColor(mBinding.ivBack, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivOrder, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivSetCover, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivDelete, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivSetting, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivDesktop, getResources().getColor(R.color.colorPrimary));

        initRecyclerViews();
        initBanner();

        mBinding.ivBack.setOnClickListener(v -> finish());
        mBinding.ivOrder.setOnClickListener(v -> toggleOrders());
        mBinding.ivSetCover.setOnClickListener(v -> onApplyImage(mModel.getCurrentImage(mBinding.banner.getCurrentItem())));
        mBinding.ivDelete.setOnClickListener(v -> mModel.deleteImage(mModel.getCurrentImage(mBinding.banner.getCurrentItem())));
        mBinding.ivSetting.setOnClickListener(v -> showBannerSetting());
        mBinding.tvStudio.setOnClickListener(v -> selectStudio());
        mBinding.ivAddOrder.setOnClickListener(v -> selectOrderToAddRecord());
        mBinding.ivAddPlayOrder.setOnClickListener(v -> onAddToPlayOrder());
//        mBinding.tvScene.setOnClickListener(v -> );
//        mBinding.ivPlay.setOnClickListener(v -> );
        mBinding.tvScore.setOnClickListener(v -> {
            if (mBinding.groupDetail.getVisibility() == View.VISIBLE) {
                mBinding.groupDetail.startAnimation(getDetailDisappear());
            }
            else {
                mBinding.groupDetail.startAnimation(getDetailAppear());
            }
        });
        mBinding.groupBottom.setOnClickListener(v -> {
            initGallery();
            recordGallery.show(getSupportFragmentManager(), "GalleryDialog");
        });

        mBinding.ivDesktop.setOnClickListener(v -> {
            showConfirmCancelMessage("即将在电脑上打开视频，是否继续？"
                    , (dialog, which) -> mModel.openOnServer()
                    , null);
        });
    }

    private void showBannerSetting() {
        BannerSettingFragment bannerSettingDialog = new BannerSettingFragment();
        bannerSettingDialog.setParams(ViewProperty.getRecordBannerParams());
        bannerSettingDialog.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onParamsUpdated(BannerParams params) {

            }

            @Override
            public void onParamsSaved(BannerParams params) {
                ViewProperty.setRecordBannerParams(params);
                BannerHelper.setBannerParams(mBinding.banner, params);
            }
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(bannerSettingDialog);
        dialogFragment.setTitle("Banner Setting");
        dialogFragment.show(getSupportFragmentManager(), "BannerSettingFragment");
    }

    private void initGallery() {
        recordGallery = new RecordGallery();
        if (pagerAdapter != null) {
            recordGallery.setCurrentPage(mBinding.banner.getCurrentItem());
        }
        recordGallery.setImageList(mModel.getImageList());
        recordGallery.setOnItemClickListener((view, position, data) -> mBinding.banner.getController().setPage(position));
    }

    private void initBanner() {
        BannerHelper.setBannerParams(mBinding.banner, ViewProperty.getRecordBannerParams());
    }

    private void initRecyclerViews() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvStars.setLayoutManager(manager);
        mBinding.rvStars.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(10);
                }
            }
        });

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvStarsDetail.setLayoutManager(manager);
        mBinding.rvStarsDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvScoreDetail.setLayoutManager(manager);
        mBinding.rvScoreDetail.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
            }
        });

        mBinding.ivPlayVideo.setOnClickListener(v -> mModel.playVideo());
        mModel.videoPlayOnReadyObserver.observe(this, result -> {
            Router.build("Player")
                    .go(RecordPadActivity.this);
        });

        mBinding.rvOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvPlayOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mBinding.rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildLayoutPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(16);
                }
            }
        });
        mBinding.ivAddTag.setOnClickListener(v -> addTag());
    }

    private void addTag() {
        TagFragment fragment = new TagFragment();
        fragment.setOnTagSelectListener(tag -> mModel.addTag(tag));
        fragment.setTagType(DataConstants.TAG_TYPE_RECORD);
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(fragment);
        dialogFragment.setTitle("Select tag");
        dialogFragment.setMaxHeight(ScreenUtils.dp2px(450));
        dialogFragment.setOnDismissListener(v -> mModel.refreshTags());
        dialogFragment.show(getSupportFragmentManager(), "TagFragment");
    }

    @Override
    protected RecordPadViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordPadViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.banner.startAutoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.banner.stopAutoPlay();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        initData();
    }

    @Override
    protected void initData() {

        mModel.recordObserver.observe(this, record -> showRecord(record));
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.passionsObserver.observe(this, list -> showPassions(list));
        mModel.scoreObserver.observe(this, list -> showScores(list));
        mModel.imagesObserver.observe(this, list -> showImages(list));
        mModel.ordersObserver.observe(this, list -> showOrders(list));
        mModel.playOrdersObserver.observe(this, list -> showPlayOrders(list));
        mModel.studioObserver.observe(this, studio -> {
            if (TextUtils.isEmpty(studio)) {
                mBinding.tvStudio.setText("Select Studio");
            }
            else {
                mBinding.tvStudio.setText(studio);
            }
        });
        mModel.videoPathObserver.observe(this, path -> {
            if (path == null) {
                mBinding.ivPlay.setVisibility(View.GONE);
            } else {
                mBinding.ivPlay.setVisibility(View.VISIBLE);
            }
        });
        mModel.paletteObserver.observe(this, palette -> updatePalette(palette));
        mModel.viewBoundsObserver.observe(this, list -> updateViewBounds(list));

        mModel.videoUrlObserver.observe(this, url -> mBinding.ivPlayVideo.setVisibility(View.VISIBLE));

        mModel.tagsObserver.observe(this, tags -> showTags(tags));

        mModel.loadRecord(getIntent().getLongExtra(EXTRA_RECORD_ID, -1));
    }

    private void showScores(List<TitleValueBean> list) {
        scoreDetailAdapter = new RecordScoreAdapter();
        scoreDetailAdapter.setList(list);
        mBinding.rvScoreDetail.setAdapter(scoreDetailAdapter);

        // it will be a little stuck as soon as activity started, it's better delay executing animation
        new Handler().postDelayed(() -> mBinding.groupDetail.startAnimation(getDetailAppear()), 1000);
    }

    private void showPassions(List<PassionPoint> list) {
        passionAdapter = new PassionPointAdapter();
        passionAdapter.setList(list);
        mBinding.groupFk.setAdapter(passionAdapter);
        mBinding.groupFk.startAnimation(getPointAnim());
    }

    private void showStars(List<RecordStar> list) {
        // 延迟一些效果更好
        new Handler().postDelayed(() -> {
            // 如果是在xml里就注册了layoutAnimation，那么延时就不起作用，所以在这里才注册anim
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(RecordPadActivity.this, R.anim.layout_pad_simple_stars);
            mBinding.rvStars.setLayoutAnimation(controller);
            starAdapter = new RecordStarAdapter();
            starAdapter.setList(list);
            starAdapter.setOnItemClickListener((view, position, data) -> goToStarPage(data.getStarId()));
            mBinding.rvStars.setAdapter(starAdapter);

            starDetailAdapter = new RecordStarDetailAdapter();
            starDetailAdapter.setList(list);
            starDetailAdapter.setOnItemClickListener((view, position, data) -> goToStarPage(data.getStarId()));
            mBinding.rvStarsDetail.setAdapter(starDetailAdapter);
        }, 1000);
    }

    private void showTags(List<Tag> tags) {
        if (tagAdapter == null) {
            tagAdapter = new TagAdapter();
            tagAdapter.setList(tags);
            tagAdapter.setOnItemLongClickListener((view, position, data) -> {
                tagAdapter.toggleDelete();
                tagAdapter.notifyDataSetChanged();
            });
            tagAdapter.setOnDeleteListener((position, bean) -> mModel.deleteTag(bean));
            mBinding.rvTags.setAdapter(tagAdapter);
        }
        else {
            tagAdapter.setList(tags);
            if (ListUtil.isEmpty(tags)) {
                tagAdapter.setShowDelete(false);
            }
            tagAdapter.notifyDataSetChanged();
        }
    }

    private void goToStarPage(long starId) {
        Router.build("StarPad")
                .with(StarPadActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

    private void selectStudio() {
        Router.build("StudioPad")
                .with(StudioPadActivity.EXTRA_SELECT_MODE, true)
                .requestCode(REQUEST_SELECT_STUDIO)
                .go(this);
    }

    private void showRecord(Record record) {
        mBinding.tvName.setText(record.getName());
        if (record.getDeprecated() == DataConstants.DEPRECATED) {
            mBinding.tvParent.setText("(Deprecated)  " + record.getDirectory());
        }
        else {
            mBinding.tvParent.setText(record.getDirectory());
        }
        mBinding.tvScore.setText(String.valueOf(record.getScore()));
        mBinding.tvBareback.setVisibility(record.getDeprecated() == DataConstants.DEPRECATED ? View.VISIBLE:View.GONE);
        mBinding.tvScene.setText(record.getScene());

        mModel.loadRecordOrders();
        mModel.loadRecordPlayOrders();
    }

    /**
     * appear animation of groupDetail
     * @return
     */
    private Animation getDetailAppear() {
        mBinding.groupDetail.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, -1.5f, Animation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(0, 1, 0, 1
                , Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        return set;
    }

    /**
     * disappear animation of groupDetail
     * @return
     */
    private Animation getDetailDisappear() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.5f);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(1, 0, 1, 0
                , Animation.RELATIVE_TO_SELF, 0.1f, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.groupDetail.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return set;
    }

    /**
     * appear animation for item of groupFk
     * @return
     */
    private Animation getPointAnim() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(1500);
        Animation scale = new ScaleAnimation(0, 1, 0, 1
                , Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 1);
        set.addAnimation(scale);
        return set;
    }

    private void showImages(List<String> list) {
        showBanner(list);
    }

    private void showBanner(List<String> list) {

        mBinding.banner.stopAutoPlay();
        List<View> viewList = new ArrayList<>();
        viewList.add(mBinding.ivBack);
        viewList.add(mBinding.ivOrder);
        viewList.add(mBinding.ivSetCover);
        viewList.add(mBinding.ivDelete);
        viewList.add(mBinding.ivSetting);
        viewList.add(mBinding.ivDesktop);
        pagerAdapter = new RecordPagerAdapter(getLifecycle());
        pagerAdapter.setViewList(viewList);
        pagerAdapter.setList(list);
        mBinding.banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                onPagePresented(position);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        pagerAdapter.setOnHolderListener(new RecordPagerAdapter.OnHolderListener() {
            @Override
            public void onPaletteCreated(int position, Palette palette) {
                mModel.cachePalette(position, palette);
                if (position == mBinding.banner.getCurrentItem()) {
                    onPagePresented(position);
                }
            }

            @Override
            public void onBoundsCreated(int position, List<ViewColorBound> bounds) {
                mModel.cacheViewBounds(position, bounds);
                if (position == mBinding.banner.getCurrentItem()) {
                    onPagePresented(position);
                }
            }
        });
        mBinding.banner.setAdapter(pagerAdapter);
        mBinding.banner.startAutoPlay();
    }

    private void onPagePresented(int position) {
        if (recordGallery != null) {
            recordGallery.setCurrentPage(position);
        }
        mModel.refreshBackground(position);
    }

    private void updateViewBounds(List<ViewColorBound> bounds) {
        if (!ListUtil.isEmpty(bounds)) {
            for (ViewColorBound bound:bounds) {
                ColorUtil.updateIconColor((ImageView) bound.view, bound.color);
            }
        }
    }

    private void updatePalette(Palette palette) {
        Palette.Swatch swatch = PaletteUtil.getDefaultSwatch(palette);
        if (swatch != null) {
            mBinding.tvName.setTextColor(swatch.getTitleTextColor());
            mBinding.tvParent.setTextColor(swatch.getBodyTextColor());
            mBinding.tvBareback.setTextColor(swatch.getBodyTextColor());
            mBinding.tvScene.setTextColor(swatch.getBodyTextColor());
            mBinding.tvScore.setTextColor(swatch.getTitleTextColor());
            mBinding.groupBottom.setBackgroundColor(swatch.getRgb());
        }
        if (palette != null) {
            passionAdapter.setSwatches(palette.getSwatches());
            mBinding.groupFk.invalidate();
        }
    }

    private void toggleOrders() {
        if (mBinding.llOrders.getVisibility() == View.VISIBLE) {
            mBinding.llOrders.startAnimation(getOrdersDisappear());
        }
        else {
            mBinding.llOrders.setVisibility(View.VISIBLE);
            mBinding.llOrders.startAnimation(getOrdersAppear());
        }
    }

    private void showOrders(List<FavorRecordOrder> list) {
        if (ordersAdapter == null) {
            ordersAdapter = new RecordOrdersAdapter();
            ordersAdapter.setTextColor(getResources().getColor(R.color.white));
            ordersAdapter.setList(list);
            ordersAdapter.setOnDeleteListener(order -> {
                mModel.deleteOrderOfRecord(order.getId());
                mModel.loadRecordOrders();
            });
            mBinding.rvOrders.setAdapter(ordersAdapter);
        }
        else {
            ordersAdapter.setList(list);
            ordersAdapter.notifyDataSetChanged();
        }
    }

    private void showPlayOrders(List<VideoPlayList> list) {
        if (playOrdersAdapter == null) {
            playOrdersAdapter = new RecordPlayOrdersAdapter();
            playOrdersAdapter.setList(list);
            playOrdersAdapter.setOnDeleteListener(order -> {
                mModel.deletePlayOrderOfRecord(order);
                mModel.loadRecordPlayOrders();
            });
            mBinding.rvPlayOrders.setAdapter(playOrdersAdapter);
        }
        else {
            playOrdersAdapter.setList(list);
            playOrdersAdapter.notifyDataSetChanged();
        }
    }

    /**
     * appear animation of orders
     * @return
     */
    private Animation getOrdersAppear() {
        mBinding.llOrders.setVisibility(View.VISIBLE);
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, -1.5f, Animation.RELATIVE_TO_SELF, 0);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(0, 1, 0, 1
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(scale);
        return set;
    }

    /**
     * disappear animation of orders
     * @return
     */
    private Animation getOrdersDisappear() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.setDuration(500);
        Animation translation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.5f);
        set.addAnimation(translation);
        Animation scale = new ScaleAnimation(1, 0, 1, 0
                , Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        set.addAnimation(scale);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mBinding.llOrders.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return set;
    }

    protected void selectOrderToAddRecord() {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SELECT_MODE, true)
                .with(OrderPhoneActivity.EXTRA_SELECT_RECORD, true)
                .requestCode(REQUEST_ADD_ORDER)
                .go(this);
    }

    private void onAddToPlayOrder() {
        Router.build("PlayOrder")
                .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                .requestCode(REQUEST_VIDEO_ORDER)
                .go(this);
    }

    private void onApplyImage(String path) {
        DebugLog.e(path);
        String[] options = new String[] {"Order", "Play Order"};
        new AlertDialogFragment()
                .setItems(options, (dialogInterface, i) -> {
                    if (i == 0) {
                        onSetCoverForOrder(path);
                    }
                    else if (i == 1) {
                        onSetCoverForPlayOrder(path);
                    }
                })
                .show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void onSetCoverForOrder(String path) {
        if (!TextUtils.isEmpty(path)) {
            Router.build("OrderPhone")
                    .with(OrderPhoneActivity.EXTRA_SET_COVER, path)
                    .go(this);
        }
    }

    private void onSetCoverForPlayOrder(String path) {
        if (!TextUtils.isEmpty(path)) {
            mModel.setUrlToSetCover(path);
            Router.build("PlayOrder")
                    .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                    .requestCode(REQUEST_SET_VIDEO_COVER)
                    .go(this);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果收不到回调，检查所在Activity是否实现了onActivityResult并且没有执行super.onActivityResult
        if (requestCode == REQUEST_ADD_ORDER) {
            if (resultCode == Activity.RESULT_OK) {
                long orderId = data.getLongExtra(AppConstants.RESP_ORDER_ID, -1);
                mModel.addToOrder(orderId);
            }
        }
        else if (requestCode == REQUEST_SELECT_STUDIO) {
            if (resultCode == RESULT_OK) {
                long orderId = data.getLongExtra(AppConstants.RESP_ORDER_ID, -1);
                mModel.addToOrder(orderId);
            }
        }
        else if (requestCode == REQUEST_VIDEO_ORDER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.addToPlay(list);
            }
        }
        else if (requestCode == REQUEST_SET_VIDEO_COVER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.setPlayOrderCover(list);
            }
        }
    }

}
