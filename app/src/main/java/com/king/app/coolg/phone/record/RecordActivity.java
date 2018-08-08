package com.king.app.coolg.phone.record;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.allure.lbanners.LMBanners;
import com.allure.lbanners.adapter.LBaseAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityRecordPhoneBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.LMBannerViewUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.content.BannerSettingFragment;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 13:25
 */
@Route("RecordPhone")
public class RecordActivity extends MvvmActivity<ActivityRecordPhoneBinding, RecordViewModel> {

    public static final String EXTRA_RECORD_ID = "key_record_id";

    private RequestOptions recordOptions;

    private RecordStarAdapter starAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_phone;
    }

    @Override
    protected void initView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mBinding.rvStars.setLayoutManager(manager);

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.groupScene.setOnClickListener(view -> {

        });
        mBinding.ivSetting.setOnClickListener(view -> {
            showSettingDialog();
        });
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
            Glide.with(RecordActivity.this)
                    .load(path)
                    .apply(recordOptions)
                    .into(mBinding.ivRecord);
        });
        mModel.imagesObserver.observe(this, list -> {
            mBinding.ivRecord.setVisibility(View.GONE);
            mBinding.banner.setVisibility(View.VISIBLE);
            showBanner(list);
        });
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.recordObserver.observe(this, record -> showRecord(record));
        mModel.passionsObserver.observe(this, list -> showPassionPoints(list));

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
    }

    private void showRecord(Record record) {
        // RecordOneVOne和RecordThree都是继承于RecordSingleScene
        if (record.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            initRecordOneVOne(record.getRecordType1v1());
        }
        else if (record.getType() == DataConstants.VALUE_RECORD_TYPE_3W) {
            initRecordThree(record.getRecordType3w());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBinding != null && mBinding.banner != null) {
            mBinding.banner.clearImageTimerTask();
        }
    }

    private class HeadBannerAdapter implements LBaseAdapter<String> {

        @Override
        public View getView(LMBanners lBanners, Context context, int position, String path) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_banner_image, null);
            ImageView imageView = view.findViewById(R.id.iv_image);

            Glide.with(RecordActivity.this)
                    .load(path)
                    .apply(recordOptions)
                    .into(imageView);
            return view;
        }
    }

}
