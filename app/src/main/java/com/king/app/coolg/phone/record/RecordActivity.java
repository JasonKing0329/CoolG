package com.king.app.coolg.phone.record;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordPhoneBinding;
import com.king.app.coolg.model.bean.BannerParams;
import com.king.app.coolg.model.bean.TitleValueBean;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.setting.ViewProperty;
import com.king.app.coolg.phone.image.ImageManagerActivity;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.studio.StudioActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.phone.video.player.PlayerActivity;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.dialog.content.TagFragment;
import com.king.app.coolg.view.helper.BannerHelper;
import com.king.app.coolg.view.widget.video.OnVideoListener;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;
import com.king.lib.banner.CoolBannerAdapter;

import java.util.ArrayList;
import java.util.List;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.PlayerManager;
import tcking.github.com.giraffeplayer2.VideoInfo;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 13:25
 */
@Route("RecordPhone")
public class RecordActivity extends MvvmActivity<ActivityRecordPhoneBinding, RecordViewModel> {

    public static final String EXTRA_RECORD_ID = "key_record_id";

    private final int REQUEST_ADD_ORDER = 1602;
    private final int REQUEST_SELECT_STUDIO = 1603;
    private final int REQUEST_VIDEO_ORDER = 1604;
    private final int REQUEST_SET_VIDEO_COVER = 1605;

    private RequestOptions recordOptions;

    private RecordStarAdapter starAdapter;

    private RecordOrdersAdapter orderAdapter;
    private RecordPlayOrdersAdapter playOrdersAdapter;

    private ScoreItemAdapter scoreAdapter;
    private TagAdapter tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_phone;
    }

    @Override
    protected void initView() {
        //set global configuration: turn on multiple_requests
        PlayerManager.getInstance().getDefaultVideoInfo().addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "multiple_requests", 1L));

        mBinding.rvStars.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mBinding.rvScores.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_banner_setting:
                    showSettingDialog();
                    break;
            }
        });
        mBinding.groupScene.setOnClickListener(view -> {

        });

        mBinding.ivOrderAdd.setOnClickListener(v -> selectOrderToAddStar());
        mBinding.ivOrderDelete.setOnClickListener(v -> {
            if (orderAdapter != null) {
                orderAdapter.toggleDeleteMode();
                orderAdapter.notifyDataSetChanged();
            }
        });
        mBinding.groupOrder.setOnClickListener(view -> {
            // collapse
            if (mBinding.ivOrderArrow.isSelected()) {
                mBinding.ivOrderArrow.setSelected(false);
                mBinding.ivOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);
                mBinding.rvOrders.setVisibility(View.GONE);
            }
            // expand
            else {
                mBinding.ivOrderArrow.setSelected(true);
                mBinding.ivOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_700_24dp);
                mBinding.rvOrders.setVisibility(View.VISIBLE);
            }
        });
        mBinding.rvOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mBinding.ivPlayOrderAdd.setOnClickListener(v -> {
            Router.build("PlayOrder")
                    .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                    .requestCode(REQUEST_VIDEO_ORDER)
                    .go(RecordActivity.this);
        });
        mBinding.ivPlayOrderDelete.setOnClickListener(v -> {
            if (playOrdersAdapter != null) {
                playOrdersAdapter.toggleDeleteMode();
                playOrdersAdapter.notifyDataSetChanged();
            }
        });
        mBinding.groupPlayOrder.setOnClickListener(view -> {
            // collapse
            if (mBinding.ivPlayOrderArrow.isSelected()) {
                mBinding.ivPlayOrderArrow.setSelected(false);
                mBinding.ivPlayOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_grey_700_24dp);
                mBinding.rvPlayOrders.setVisibility(View.GONE);
            }
            // expand
            else {
                mBinding.ivPlayOrderArrow.setSelected(true);
                mBinding.ivPlayOrderArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_grey_700_24dp);
                mBinding.rvPlayOrders.setVisibility(View.VISIBLE);
            }
        });
        mBinding.rvPlayOrders.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

//        mBinding.ivSetCover.setOnClickListener(view -> {
//            if (!TextUtils.isEmpty(mModel.getSingleImagePath())) {
//                onApplyImage(mModel.getSingleImagePath());
//            }
//        });
        mBinding.groupStudio.setOnClickListener(view -> selectStudio());

        mBinding.scrollParent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (mBinding.videoView.getVisibility() == View.VISIBLE && mBinding.videoView.getPlayer().isPlaying()) {
                floatOrEmbedVideo(oldScrollY, scrollY, mBinding.videoView.getHeight());
            }
        });

        mBinding.ivDesktop.setOnClickListener(v -> {
            showConfirmCancelMessage("即将在电脑上打开视频，是否继续？"
                    , (dialog, which) -> mModel.openOnServer()
                    , null);
        });

        mBinding.videoView.interceptFullScreenListener(v -> {
            showConfirmCancelMessage("是否在临时列表中打开，若是，视频将从上一次记录的位置开始播放？"
                    , getString(R.string.yes), (dialog, which) -> mModel.playInPlayer()
                    , getString(R.string.no), (dialog, which) -> mBinding.videoView.executeFullScreen());
        });

        mBinding.rvTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int position = parent.getChildLayoutPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(10);
                }
            }
        });
        mBinding.ivTagAdd.setOnClickListener(v -> addTag());
        mBinding.ivTagDelete.setOnClickListener(v -> {
            tagAdapter.toggleDelete();
            tagAdapter.notifyDataSetChanged();
        });

        mBinding.ivMore.setOnClickListener(v -> {
            Router.build("ImageManager")
                    .with(ImageManagerActivity.EXTRA_TYPE, ImageManagerActivity.TYPE_RECORD)
                    .with(ImageManagerActivity.EXTRA_DATA, getRecordId())
                    .go(RecordActivity.this);
        });
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

    private void floatOrEmbedVideo(int oldScrollY, int scrollY, int edge) {
        // 上滑且超出边界
        if (scrollY > edge && scrollY > oldScrollY
                && mBinding.videoView.getPlayer().getDisplayModel() != GiraffePlayer.DISPLAY_FLOAT) {
            VideoInfo.floatView_width = getResources().getDimensionPixelSize(R.dimen.float_video_width);
            VideoInfo.floatView_width = getResources().getDimensionPixelSize(R.dimen.float_video_height);
            mBinding.videoView.getPlayer().setDisplayModel(GiraffePlayer.DISPLAY_FLOAT);
        }
        // 下滑且超出边界
        else if (scrollY <= edge && scrollY < oldScrollY
                && mBinding.videoView.getPlayer().getDisplayModel() != GiraffePlayer.DISPLAY_NORMAL) {
            mBinding.videoView.getPlayer().setDisplayModel(GiraffePlayer.DISPLAY_NORMAL);
        }
    }

    private void selectStudio() {
        Router.build("StudioPhone")
                .with(StudioActivity.EXTRA_SELECT_MODE, true)
                .requestCode(REQUEST_SELECT_STUDIO)
                .go(this);
    }

    @Override
    protected RecordViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordViewModel.class);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mModel.loadRecord(intent.getLongExtra(EXTRA_RECORD_ID, -1));
    }

    private void setNoImage() {
        mBinding.banner.setVisibility(View.GONE);
        mBinding.guideView.setVisibility(View.GONE);
        mBinding.ivRecord.setVisibility(View.VISIBLE);
        Glide.with(RecordActivity.this)
                .load(R.drawable.def_small)
                .into(mBinding.ivRecord);
    }

    private void setSingleImage(String path) {
        mBinding.banner.setVisibility(View.GONE);
        mBinding.guideView.setVisibility(View.GONE);
        mBinding.ivRecord.setVisibility(View.VISIBLE);
        Glide.with(RecordActivity.this)
                .load(path)
                .apply(recordOptions)
                .into(mBinding.ivRecord);
    }

    @Override
    protected void initData() {

        recordOptions = GlideUtil.getRecordOptions();

        mModel.imagesObserver.observe(this, list -> {
            if (list.size() == 0) {
                setNoImage();
            }
            else if (list.size() == 1) {
                setSingleImage(list.get(0));
            }
            else {
                mBinding.ivRecord.setVisibility(View.GONE);
                mBinding.banner.setVisibility(View.VISIBLE);
                mBinding.guideView.setVisibility(View.VISIBLE);
                showBanner(list);
            }
        });
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.recordObserver.observe(this, record -> {
            showRecord(record);
            mModel.loadRecordOrders();
            mModel.loadRecordPlayOrders();
        });
        mModel.scoresObserver.observe(this, list -> showScores(list));
        mModel.ordersObserver.observe(this, list -> {
            mBinding.tvOrder.setText(String.valueOf(list.size()));
            if (orderAdapter == null) {
                orderAdapter = new RecordOrdersAdapter();
                orderAdapter.setOnDeleteListener(order -> {
                    mModel.deleteOrderOfRecord(order.getId());
                    mModel.loadRecordOrders();
                });
                orderAdapter.setList(list);
                mBinding.rvOrders.setAdapter(orderAdapter);
            }
            else {
                orderAdapter.setList(list);
                orderAdapter.notifyDataSetChanged();
            }
        });
        mModel.playOrdersObserver.observe(this, list -> {
            mBinding.tvPlayOrder.setText(String.valueOf(list.size()));
            if (playOrdersAdapter == null) {
                playOrdersAdapter = new RecordPlayOrdersAdapter();
                playOrdersAdapter.setOnDeleteListener(order -> {
                    mModel.deletePlayOrderOfRecord(order);
                    mModel.loadRecordPlayOrders();
                });
                playOrdersAdapter.setList(list);
                mBinding.rvPlayOrders.setAdapter(playOrdersAdapter);
            }
            else {
                playOrdersAdapter.setList(list);
                playOrdersAdapter.notifyDataSetChanged();
            }
        });
        mModel.passionsObserver.observe(this, list -> showPassionPoints(list));
        mModel.studioObserver.observe(this, studio -> mBinding.tvStudio.setText(studio));

        mModel.videoUrlObserver.observe(this, url -> previewVideo(url));

        mModel.playVideoInPlayer.observe(this, item -> playList());

        mModel.bitmapObserver.observe(this, bitmap -> {
            mBinding.banner.setVisibility(View.GONE);
            mBinding.videoView.setVisibility(View.VISIBLE);
            mBinding.videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(this)
                    .load(bitmap)
                    .apply(recordOptions)
                    .into(mBinding.videoView.getCoverView());
        });

        mModel.tagsObserver.observe(this, tags -> showTags(tags));

        mModel.loadRecord(getRecordId());
    }

    private long getRecordId() {
        return getIntent().getLongExtra(EXTRA_RECORD_ID, -1);
    }

    private void showTags(List<Tag> tags) {
        if (ListUtil.isEmpty(tags)) {
            mBinding.ivTagDelete.setVisibility(View.GONE);
            mBinding.tvTagsTitle.setVisibility(View.VISIBLE);
        }
        else {
            mBinding.ivTagDelete.setVisibility(View.VISIBLE);
            mBinding.tvTagsTitle.setVisibility(View.GONE);
        }
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

    private void playList() {
        Router.build("Player")
                .with(PlayerActivity.EXTRA_ORDER_ID, AppConstants.PLAY_ORDER_TEMP_ID)
                .with(PlayerActivity.EXTRA_PLAY_RANDOM, false)
                .with(PlayerActivity.EXTRA_PLAY_LAST, true)
                .go(this);
    }

    private void showScores(List<TitleValueBean> list) {
        if (scoreAdapter == null) {
            scoreAdapter = new ScoreItemAdapter();
            scoreAdapter.setList(list);
            mBinding.rvScores.setAdapter(scoreAdapter);
        }
        else {
            scoreAdapter.setList(list);
            scoreAdapter.notifyDataSetChanged();
        }
    }

    private void showPassionPoints(List<PassionPoint> list) {
        PassionPointAdapter adapter = new PassionPointAdapter();
        adapter.setList(list);
        mBinding.groupFk.setAdapter(adapter);
    }

    private void showBanner(List<String> list) {
        BannerHelper.setBannerParams(mBinding.banner, ViewProperty.getRecordBannerParams());

        HeadBannerAdapter adapter = new HeadBannerAdapter();
        adapter.setList(list);

        mBinding.guideView.setPointNumber(list.size());
        mBinding.guideView.setGuideTextGravity(Gravity.CENTER);
        mBinding.banner.setOnBannerPageListener((page, adapterIndex) -> mBinding.guideView.setFocusIndex(page));

        mBinding.banner.setAdapter(adapter);
        mBinding.banner.startAutoPlay();
    }

    private void showStars(List<RecordStar> list) {
        starAdapter = new RecordStarAdapter();
        starAdapter.setList(list);
        starAdapter.setOnItemClickListener((view, position, data) -> goToStarPage(data));
        mBinding.rvStars.setAdapter(starAdapter);
    }

    private void goToStarPage(RecordStar data) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, data.getStarId())
                .go(this);
    }

    private void showRecord(Record record) {
        // Record公共部分
        mBinding.tvDate.setText(FormatUtil.formatDate(record.getLastModifyTime()));
        mBinding.tvScene.setText(record.getScene());
        mBinding.tvPath.setText(record.getDirectory() + "/" + record.getName());
        mBinding.tvHd.setText("" + record.getHdLevel());
        mBinding.tvScoreTotal.setText("" + record.getScore());
        mBinding.tvFeel.setText("" + record.getScoreFeel());
        if (record.getScoreBareback() > 0) {
            mBinding.groupBareback.setVisibility(View.VISIBLE);
        } else {
            mBinding.groupBareback.setVisibility(View.GONE);
        }
        mBinding.tvCum.setText("" + record.getScoreCum());
        mBinding.tvSpecial.setText("" + record.getScoreSpecial());
        if (TextUtils.isEmpty(record.getSpecialDesc())) {
            mBinding.groupSpecial.setVisibility(View.GONE);
        } else {
            mBinding.groupSpecial.setVisibility(View.VISIBLE);
            mBinding.tvSpecialContent.setText(record.getSpecialDesc());
        }
        mBinding.tvFk.setText("Passion(" + record.getScorePassion() + ")");
        mBinding.tvStar.setText("" + record.getScoreStar());
        mBinding.tvBody.setText("" + record.getScoreBody());
        mBinding.tvCock.setText("" + record.getScoreCock());
        mBinding.tvAss.setText("" + record.getScoreAss());

        mBinding.tvDeprecated.setVisibility(record.getDeprecated() == 1 ? View.VISIBLE : View.GONE);

        String cuPath = ImageProvider.getRecordCuPath(record.getName());
        if (!TextUtils.isEmpty(cuPath)) {
            mBinding.ivCum.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .asGif()
                    .load(cuPath)
                    .into(mBinding.ivCum);
        }
    }

    private void showSettingDialog() {
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

    /**
     * init video player
     * @param url
     */
    private void previewVideo(String url) {
        mBinding.banner.setVisibility(View.GONE);
        mBinding.videoView.setVisibility(View.VISIBLE);
        mBinding.videoView.getCoverView().setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(this)
                .load(mModel.getVideoCover())
                .apply(recordOptions)
                .into(mBinding.videoView.getCoverView());
        // 本地没有图片，从网络视频获取帧图片
        if (mModel.getVideoCover() == null) {
            mModel.loadVideoBitmap();
        }
        mBinding.videoView.setVideoPath(url);

        mBinding.videoView.setOnVideoListener(new OnVideoListener() {
            @Override
            public int getStartSeek() {
                return mModel.getVideoStartSeek();
            }

            @Override
            public void updatePlayPosition(int currentPosition) {
                mModel.updatePlayPosition(currentPosition);
            }

            @Override
            public void onPlayComplete() {
                mModel.resetPlayInDb();
            }

            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {
                mModel.updatePlayToDb();
            }

            @Override
            public void onDestroy() {
                mModel.updatePlayToDb();
            }
        });
        mBinding.videoView.prepare();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // videoView必须在manifest中所属activity指定
        // android:configChanges="orientation|screenSize",且其中两个参数缺一不可
        // 同时在onConfigurationChanged中加入相关代码。
        // 这样在点击全屏时才能顺畅地切换为全屏
        PlayerManager.getInstance().onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (PlayerManager.getInstance().onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.stopAutoPlay();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.startAutoPlay();
        }
    }

    private void onSetCoverForOrder(String path) {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SET_COVER, path)
                .go(this);
    }

    private void onSetCoverForPlayOrder(String path) {
        mModel.setUrlToSetCover(path);
        Router.build("PlayOrder")
                .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                .requestCode(REQUEST_SET_VIDEO_COVER)
                .go(this);
    }

    private void selectOrderToAddStar() {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SELECT_MODE, true)
                .with(OrderPhoneActivity.EXTRA_SELECT_RECORD, true)
                .requestCode(REQUEST_ADD_ORDER)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_ORDER) {
            if (resultCode == RESULT_OK) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.stopAutoPlay();
        }
        if (mModel != null) {
            mModel.updatePlayToDb();
        }
    }

    private void onApplyImage(String path) {
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

    private class HeadBannerAdapter extends CoolBannerAdapter<String> {

        @Override
        protected int getLayoutRes() {
            return R.layout.adapter_banner_image;
        }

        @Override
        protected void onBindView(View view, int position, String path) {
            ImageView imageView = view.findViewById(R.id.iv_image);

            Glide.with(view.getContext())
                    .load(path)
                    .apply(recordOptions)
                    .into(imageView);
        }
    }
}
