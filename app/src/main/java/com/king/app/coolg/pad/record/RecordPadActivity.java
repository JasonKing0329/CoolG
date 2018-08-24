package com.king.app.coolg.pad.record;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.king.app.coolg.databinding.ActivityRecordPadBinding;
import com.king.app.coolg.model.palette.PaletteUtil;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.app.coolg.pad.star.StarPadActivity;
import com.king.app.coolg.phone.record.PassionPoint;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.param.DataConstants;

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

    private RecordStarAdapter starAdapter;
    private RecordStarDetailAdapter starDetailAdapter;
    private RecordScoreAdapter scoreDetailAdapter;
    private PassionPointAdapter passionAdapter;

    private RecordGallery recordGallery;
    private boolean isFirstTimeLoadFirstPage = true;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_pad;
    }

    @Override
    protected void initView() {
        ColorUtil.updateIconColor(mBinding.ivBack, getResources().getColor(R.color.colorPrimary));
        ColorUtil.updateIconColor(mBinding.ivOrder, getResources().getColor(R.color.colorPrimary));

        initRecyclerViews();
        initBanner();

        mBinding.ivBack.setOnClickListener(v -> finish());
//        mBinding.tvScene.setOnClickListener(v -> );
//        mBinding.ivOrder.setOnClickListener(v -> );
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
            initPager();
            recordGallery.show(getSupportFragmentManager(), "GalleryDialog");
        });
    }

    private void initPager() {
        recordGallery = new RecordGallery();
        recordGallery.setCurrentPage(mBinding.banner.getCurrentItem());
        recordGallery.setImageList(mModel.getImageList());
        recordGallery.setOnItemClickListener((view, position, data) -> mBinding.banner.getViewPager().setCurrentItem(position));
    }

    private void initBanner() {
        mBinding.banner.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                // 第一次触发时，holder里image还没有加载完成，presenter里还没有缓存
                if (isFirstTimeLoadFirstPage && position == 0) {
                    isFirstTimeLoadFirstPage = false;
                    new Handler().postDelayed(() -> {
                        if (recordGallery != null) {
                            recordGallery.setCurrentPage(position);
                        }
                        mModel.refreshBackground(position);
                    }, 900);
                }
                else {
                    if (recordGallery != null) {
                        recordGallery.setCurrentPage(position);
                    }
                    mModel.refreshBackground(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
    }

    @Override
    protected RecordPadViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordPadViewModel.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBinding.banner.startTurning(5000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.banner.stopTurning();
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
        mModel.videoPathObserver.observe(this, path -> {
            if (path == null) {
                mBinding.ivPlay.setVisibility(View.GONE);
            } else {
                mBinding.ivPlay.setVisibility(View.VISIBLE);
            }
        });
        mModel.paletteObserver.observe(this, palette -> updatePalette(palette));
        mModel.viewBoundsObserver.observe(this, list -> updateViewBounds(list));

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

    private void goToStarPage(long starId) {
        Router.build("StarPad")
                .with(StarPadActivity.EXTRA_STAR_ID, starId)
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
        mBinding.banner.setPages(() -> {
            List<View> viewList = new ArrayList<>();
            viewList.add(mBinding.ivBack);
            viewList.add(mBinding.ivOrder);
            RecordImageHolder holder = new RecordImageHolder(getLifecycle(), viewList);
            holder.setOnHolderListener(new RecordImageHolder.OnHolderListener() {
                @Override
                public void onPaletteCreated(int position, Palette palette) {
                    mModel.cachePalette(position, palette);
                }

                @Override
                public void onBoundsCreated(int position, List<ViewColorBound> bounds) {
                    mModel.cacheViewBounds(position, bounds);
                }
            });
            return holder;
        }, list);
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

}