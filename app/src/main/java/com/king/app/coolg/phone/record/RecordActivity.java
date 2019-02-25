package com.king.app.coolg.phone.record;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordPhoneBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.studio.StudioActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.coolg.view.widget.video.OnVideoListener;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tcking.github.com.giraffeplayer2.GiraffePlayer;
import tcking.github.com.giraffeplayer2.Option;
import tcking.github.com.giraffeplayer2.PlayerManager;
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

    private RequestOptions recordOptions;

    private RecordStarAdapter starAdapter;

    private RecordOrdersAdapter orderAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_phone;
    }

    @Override
    protected void initView() {
        //set global configuration: turn on multiple_requests
        PlayerManager.getInstance().getDefaultVideoInfo().addOption(Option.create(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "multiple_requests", 1L));

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvStars.setLayoutManager(manager);

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
        mBinding.rvOrders.setLayoutManager(new LinearLayoutManager(mBinding.rvOrders.getContext(), LinearLayoutManager.HORIZONTAL, false));

        mBinding.ivDelete.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mModel.getSingleImagePath())) {
                onDeleteImage(mModel.getSingleImagePath());
            }
        });
        mBinding.ivSetCover.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(mModel.getSingleImagePath())) {
                onApplyImage(mModel.getSingleImagePath());
            }
        });
        mBinding.groupStudio.setOnClickListener(view -> selectStudio());

        mBinding.scrollParent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (mBinding.videoView.getVisibility() == View.VISIBLE && mBinding.videoView.getPlayer().isPlaying()) {
                floatOrEmbedVideo(oldScrollY, scrollY, mBinding.videoView.getHeight());
            }
        });

        mBinding.groupAddToPlay.setOnClickListener(v -> {
            Router.build("PlayOrder")
                    .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                    .requestCode(REQUEST_VIDEO_ORDER)
                    .go(RecordActivity.this);
        });

    }

    private void floatOrEmbedVideo(int oldScrollY, int scrollY, int edge) {
        // 上滑且超出边界
        if (scrollY > edge && scrollY > oldScrollY
                && mBinding.videoView.getPlayer().getDisplayModel() != GiraffePlayer.DISPLAY_FLOAT) {
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

    @Override
    protected void initData() {

        recordOptions = GlideUtil.getRecordOptions();

        mModel.singleImageObserver.observe(this, path -> {
            mBinding.banner.setVisibility(View.GONE);
            mBinding.ivRecord.setVisibility(View.VISIBLE);
            mBinding.ivSetCover.setVisibility(View.VISIBLE);
            mBinding.ivDelete.setVisibility(View.VISIBLE);
            Glide.with(RecordActivity.this)
                    .load(path)
                    .apply(recordOptions)
                    .into(mBinding.ivRecord);
        });
        mModel.imagesObserver.observe(this, list -> {
            mBinding.ivRecord.setVisibility(View.GONE);
            mBinding.ivSetCover.setVisibility(View.GONE);
            mBinding.ivDelete.setVisibility(View.GONE);
            mBinding.banner.setVisibility(View.VISIBLE);
            showBanner(list);
        });
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.recordObserver.observe(this, record -> {
            showRecord(record);
            mModel.loadRecordOrders();
        });
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
        mModel.passionsObserver.observe(this, list -> showPassionPoints(list));
        mModel.studioObserver.observe(this, studio -> mBinding.tvStudio.setText(studio));

        mModel.videoUrlObserver.observe(this, url -> previewVideo(url));

        mModel.loadRecord(getIntent().getLongExtra(EXTRA_RECORD_ID, -1));
    }

    private void showPassionPoints(List<PassionPoint> list) {
        PassionPointAdapter adapter = new PassionPointAdapter();
        adapter.setList(list);
        mBinding.groupFk.setAdapter(adapter);
    }

    private void showBanner(List<String> list) {
        setBannerParams();
        HeadBannerAdapter adapter = new HeadBannerAdapter();
        mBinding.banner.setAdapter(adapter, list);
    }

    private void setBannerParams() {
        // 禁用btnStart(只在onPageScroll触发后有效)
        mBinding.banner.isGuide(false);
        // 显示引导圆点
//        lmBanners.hideIndicatorLayout();
        mBinding.banner.setIndicatorPosition(LMBanners.IndicaTorPosition.BOTTOM_MID);
        // 可以不写，因为文件名直接覆用的mBinding.banner-1.0.8里的res
        mBinding.banner.setSelectIndicatorRes(R.drawable.page_indicator_select);
        mBinding.banner.setUnSelectUnIndicatorRes(R.drawable.page_indicator_unselect);
        // 轮播切换时间
        mBinding.banner.setDurtion(SettingProperty.getRecommendAnimTime());
        if (SettingProperty.isRandomRecommend()) {
            Random random = new Random();
            int type = Math.abs(random.nextInt()) % LMBannerViewUtil.ANIM_TYPES.length;
            LMBannerViewUtil.setScrollAnim(mBinding.banner, type);
        } else {
            LMBannerViewUtil.setScrollAnim(mBinding.banner, SettingProperty.getRecommendAnimType());
        }
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
        // RecordOneVOne和RecordThree都是继承于RecordSingleScene
        switch (record.getType()) {
            case DataConstants.VALUE_RECORD_TYPE_1V1:
                initRecordOneVOne(record.getRecordType1v1());
                break;
            case DataConstants.VALUE_RECORD_TYPE_3W:
            case DataConstants.VALUE_RECORD_TYPE_MULTI:
            case DataConstants.VALUE_RECORD_TYPE_LONG:
                initRecordThree(record.getRecordType3w());
                break;
        }

        // Record公共部分
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

        mBinding.tvDeprecated.setVisibility(record.getDeprecated() == 1 ? View.VISIBLE : View.GONE);

//        videoPath = VideoModel.getVideoPath(record.getName());
//        videoPath = "/storage/emulated/0/tencent/MicroMsg/WeiXin/wx_camera_1489199749192.mp4";
//        if (videoPath == null) {
//            mBinding.ivPlay.setVisibility(View.GONE);
//        } else {
//            mBinding.ivPlay.setVisibility(View.VISIBLE);
//        }
        mBinding.ivPlay.setVisibility(View.GONE);

        String cuPath = ImageProvider.getRecordCuPath(record.getName());
        if (!TextUtils.isEmpty(cuPath)) {
            mBinding.ivCum.setVisibility(View.VISIBLE);
            GlideApp.with(this)
                    .asGif()
                    .load(cuPath)
                    .into(mBinding.ivCum);
        }
    }

    private void initRecordOneVOne(RecordType1v1 record) {
        mBinding.tvStory.setText("" + record.getScoreStory());
        mBinding.tvSceneScore.setText("" + record.getScoreScene());
        mBinding.tvBjob.setText("" + record.getScoreBjob());
        mBinding.tvRhythm.setText("" + record.getScoreRhythm());
        mBinding.tvForeplay.setText("" + record.getScoreForePlay());
        mBinding.tvRim.setText("" + record.getScoreRim());
        mBinding.tvCshow.setText("" + record.getScoreCshow());
    }

    private void initRecordThree(RecordType3w record) {
        mBinding.tvStory.setText("" + record.getScoreStory());
        mBinding.tvSceneScore.setText("" + record.getScoreScene());
        mBinding.tvStory.setText("" + record.getScoreStory());
        mBinding.tvSceneScore.setText("" + record.getScoreScene());
        mBinding.tvBjob.setText("" + record.getScoreBjob());
        mBinding.tvRhythm.setText("" + record.getScoreRhythm());
        mBinding.tvForeplay.setText("" + record.getScoreForePlay());
        mBinding.tvRim.setText("" + record.getScoreRim());
        mBinding.tvCshow.setText("" + record.getScoreCshow());
    }

    private void showSettingDialog() {
        BannerSettingFragment bannerSettingDialog = new BannerSettingFragment();
        bannerSettingDialog.setOnAnimSettingListener(new BannerSettingFragment.OnAnimSettingListener() {
            @Override
            public void onRandomAnim(boolean random) {
                SettingProperty.setRandomRecommend(random);
            }

            @Override
            public boolean isRandomAnim() {
                return SettingProperty.isRandomRecommend();
            }

            @Override
            public int getAnimType() {
                return SettingProperty.getRecommendAnimType();
            }

            @Override
            public void onSaveAnimType(int type) {
                SettingProperty.setRecommendAnimType(type);
            }

            @Override
            public int getAnimTime() {
                return SettingProperty.getRecommendAnimTime();
            }

            @Override
            public void onSaveAnimTime(int time) {
                SettingProperty.setRecommendAnimTime(time);
            }

            @Override
            public void onParamsSaved() {
                setBannerParams();
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
            mBinding.banner.stopImageTimerTask();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.startImageTimerTask();
        }
    }

    private void onApplyImage(String path) {
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SET_COVER, path)
                .go(this);
    }

    private void onDeleteImage(String path) {
        showConfirmCancelMessage("Are you sure to delete this image on file system?",
                (dialogInterface, i) -> mModel.deleteImage(path), null);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.clearImageTimerTask();
        }
        if (mModel != null) {
            mModel.updatePlayToDb();
        }
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_image, null);
            ImageView imageView = view.findViewById(R.id.iv_image);
            ImageView ivCover = view.findViewById(R.id.iv_set_cover);
            ImageView ivDelete = view.findViewById(R.id.iv_delete);
            ivCover.setVisibility(View.VISIBLE);
            ivDelete.setVisibility(View.VISIBLE);

            Glide.with(context)
                    .load(path)
                    .apply(recordOptions)
                    .into(imageView);

            ivCover.setOnClickListener(v -> {
                onApplyImage(path);
            });
            ivDelete.setOnClickListener(v -> {
                onDeleteImage(path);
            });
            return view;
        }
    }
}
