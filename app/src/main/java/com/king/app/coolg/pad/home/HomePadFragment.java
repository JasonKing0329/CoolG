package com.king.app.coolg.pad.home;

import android.animation.Animator;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentHomePadBinding;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.pad.star.StarPadActivity;
import com.king.app.coolg.phone.home.RecommendFilterFragment;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.GlideUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.widget.cardslider.CardSnapHelper;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 10:32
 */
public class HomePadFragment extends MvvmFragment<FragmentHomePadBinding, HomePadViewModel> {

    private HomeStarAdapter adapter;

    private RequestOptions recordOption;

    private HomeRecordPadAdapter homeRecordAdapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_home_pad;
    }

    @Override
    protected HomePadViewModel createViewModel() {
        return ViewModelProviders.of(this).get(HomePadViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.srRefresh.setOnRefreshListener(() -> mModel.refreshRecAndStars());

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return homeRecordAdapter.getSpanSize(position);
            }
        });
        mBinding.rvRecords.setLayoutManager(manager);
        mBinding.rvRecords.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvRecords.setEnableLoadMore(true);
        mBinding.rvRecords.setOnLoadMoreListener(() -> mModel.loadMore());

        updateIconBg(mBinding.tvIconRecord);
        updateIconBg(mBinding.tvIconStar);
        updateIconBg(mBinding.tvIconSurf);

        mBinding.ivRec1.setOnClickListener(v -> goToRecordPage(mModel.getRecommendedRecord(0)));
        mBinding.ivRec2.setOnClickListener(v -> goToRecordPage(mModel.getRecommendedRecord(1)));
        mBinding.ivRec3.setOnClickListener(v -> goToRecordPage(mModel.getRecommendedRecord(2)));

        mBinding.groupRecord.setOnClickListener(v -> goToRecordListPage());
        mBinding.groupStar.setOnClickListener(v -> goToStarListPage());
        mBinding.fabSetting.setOnClickListener(v -> showBannerSetting());
        mBinding.fabTop.setOnClickListener(v -> mBinding.rvRecords.scrollToPosition(0));
    }

    private void showBannerSetting() {
        RecommendFilterFragment content = new RecommendFilterFragment();
        content.setOnRecordFilterListener(model -> {
            mModel.updateRecordFilter(model);
            mModel.resetTimer();
            mModel.refreshRec();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight());
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.show(getChildFragmentManager(), "RecommendFilterFragment");
    }

    private void updateIconBg(TextView view) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(ColorUtil.randomWhiteTextBgColor());
        view.setBackground(drawable);
    }

    @Override
    protected void onCreateData() {
        recordOption = GlideUtil.getRecordAnimOptions();

        mModel.recommendsObserver.observe(this, list -> showRecommends(list));
        mModel.starsObserver.observe(this, list -> showStars(list));
        mModel.recordsObserver.observe(this, list -> showRecords(list));
        mModel.moreObserver.observe(this, position -> homeRecordAdapter.notifyItemInserted(position));

        mModel.loadHomeData();
    }

    private void showStars(List<StarProxy> list) {
        if (adapter == null) {
            adapter = new HomeStarAdapter();
            adapter.setOnItemClickListener((view, position, data) -> goToStarPage(data));
            adapter.setList(list);
            mBinding.rvStars.setAdapter(adapter);
            // 只能attach一次
            new CardSnapHelper().attachToRecyclerView(mBinding.rvStars);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
        mBinding.rvStars.scrollToPosition(0);
    }

    private void showRecommends(List<Record> list) {
        if (mBinding.srRefresh.isRefreshing()) {
            mBinding.srRefresh.setRefreshing(false);
        }
        startRevealView(1000);
        try {
            Glide.with(getActivity())
                    .load(ImageProvider.getRecordRandomPath(list.get(0).getName(), null))
                    .apply(recordOption)
                    .into(mBinding.ivRec1);
            Glide.with(getActivity())
                    .load(ImageProvider.getRecordRandomPath(list.get(1).getName(), null))
                    .apply(recordOption)
                    .into(mBinding.ivRec2);
            Glide.with(getActivity())
                    .load(ImageProvider.getRecordRandomPath(list.get(2).getName(), null))
                    .apply(recordOption)
                    .into(mBinding.ivRec3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showRecords(List<Object> list) {
        if (homeRecordAdapter == null) {
            homeRecordAdapter = new HomeRecordPadAdapter();
            homeRecordAdapter.setList(list);
            homeRecordAdapter.setOnListListener((position, record) -> goToRecordPage(record));
            mBinding.rvRecords.setAdapter(homeRecordAdapter);
        }
        else {
            homeRecordAdapter.setList(list);
            homeRecordAdapter.notifyDataSetChanged();
        }
    }

    private void startRevealView(int animTime) {
        Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.srRefresh, (int) mBinding.srRefresh.getX()
                , (int) mBinding.srRefresh.getY() + mBinding.srRefresh.getHeight()
                , 0, (float) Math.hypot(mBinding.srRefresh.getWidth(), mBinding.srRefresh.getHeight()));
        anim.setDuration(animTime);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void goToStarPage(StarProxy data) {
        Router.build("StarPad")
                .with(StarPadActivity.EXTRA_STAR_ID, data.getStar().getId())
                .go(this);
    }

    private void goToRecordPage(Record data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
    }

    private void goToRecordListPage() {
        Router.build("RecordListPad")
                .go(this);
    }

    private void goToStarListPage() {
        Router.build("StarListPad")
                .go(this);
    }

}
