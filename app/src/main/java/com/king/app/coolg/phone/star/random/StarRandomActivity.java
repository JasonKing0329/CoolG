package com.king.app.coolg.phone.star.random;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.GlideRequest;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityStarRandomBinding;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.phone.star.list.StarSelectorActivity;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.gdb.data.entity.Star;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/7 10:25
 */
@Route("StarRandom")
public class StarRandomActivity extends MvvmActivity<ActivityStarRandomBinding, StarRandomViewModel> {

    public static final int REQUEST_SELECT_STAR = 421;

    private CandidateAdapter candidateAdapter;
    private CandidateAdapter selectedAdapter;

    @Override
    protected StarRandomViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarRandomViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_star_random;
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvSelected.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.btnRule.setOnClickListener(v -> {
            mModel.stopRandom();
            showRuleSetting();
        });
        mBinding.btnMark.setOnClickListener(v -> {
            mModel.stopRandom();
            mModel.markCurrentStar();
        });
        mBinding.btnReset.setOnClickListener(v -> {
            mModel.stopRandom();
            showConfirmCancelMessage("This action will clear all data in current page, continue?"
                    , (dialog, which) -> mModel.clearAll()
                    , null);
        });
        mBinding.ivStar.setOnClickListener(v -> {
            if (mModel.getCurrentStar() != null) {
                Router.build("StarPhone")
                        .with(StarActivity.EXTRA_STAR_ID, mModel.getCurrentStar().getStar().getId())
                        .go(this);
            }
        });

        int color = mModel.getIconColor();
        ColorUtil.updateIconColor(mBinding.btnReset, color);
        ColorUtil.updateIconColor(mBinding.btnMark, color);
        ColorUtil.updateIconColor(mBinding.btnRule, color);
        ColorUtil.updateIconColor(mBinding.btnStart, color);

        onDeleteModeChanged();
    }

    @Override
    protected void initData() {
        mModel.candidatesObserver.observe(this, list -> showCandidates(list));
        mModel.selectedObserver.observe(this, list -> showSelectedList(list));
        mModel.starObserver.observe(this, star -> showStar(star));
        mModel.loadDefaultData();
    }

    private void showRuleSetting() {
        RandomSettingFragment content = new RandomSettingFragment();
        content.setRandomRule(mModel.getRandomRule());
        content.setOnSettingListener(randomRule -> mModel.saveRandomRule(randomRule));
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Random rules");
        dialogFragment.show(getSupportFragmentManager(), "RandomSettingFragment");
    }

    private void showStar(StarProxy star) {
        GlideRequest request = GlideApp.with(this)
                .load(star.getImagePath());
        if (star.getWidth() != 0 && star.getHeight() != 0) {
            request.override(star.getWidth(), star.getHeight());
        }
        request
                .error(R.drawable.def_person_square)
                .into(mBinding.ivStar);
    }

    private void showCandidates(List<Star> list) {
        if (candidateAdapter == null) {
            candidateAdapter = new CandidateAdapter();
            candidateAdapter.setList(list);
            candidateAdapter.setOnDeleteListener((position, star) -> {
                mModel.deleteCandidate(star);
            });
            candidateAdapter.setOnItemLongClickListener((view, position, data) -> {
                candidateAdapter.setDeleteMode(!candidateAdapter.isDeleteMode());
                onDeleteModeChanged();
                candidateAdapter.notifyDataSetChanged();
            });
            mBinding.rvList.setAdapter(candidateAdapter);
        }
        else {
            candidateAdapter.setList(list);
            candidateAdapter.notifyDataSetChanged();
        }
        if (list.size() > 0) {
            mBinding.tvList.setVisibility(View.GONE);
        }
        else {
            mBinding.tvList.setVisibility(View.VISIBLE);
            candidateAdapter.setDeleteMode(false);
            onDeleteModeChanged();
        }
    }

    private void onDeleteModeChanged() {
        if (candidateAdapter != null && candidateAdapter.isDeleteMode()) {
            mBinding.ivRange.setImageResource(R.drawable.ic_delete_grey_600_24dp);
            mBinding.ivRange.setOnClickListener(v -> {
                mModel.clearCandidates();
                candidateAdapter.setDeleteMode(false);
                onDeleteModeChanged();
            });
        }
        else {
            mBinding.ivRange.setImageResource(R.drawable.ic_add_grey_600_36dp);
            mBinding.ivRange.setOnClickListener(v -> {
                Router.build("StarSelector")
                        .requestCode(REQUEST_SELECT_STAR)
                        .go(this);
            });
        }
    }

    private void showSelectedList(List<Star> list) {
        if (selectedAdapter == null) {
            selectedAdapter = new CandidateAdapter();
            selectedAdapter.setList(list);
            selectedAdapter.setOnDeleteListener((position, star) -> {
                mModel.deleteSelected(star);
            });
            selectedAdapter.setOnItemLongClickListener((view, position, data) -> {
                selectedAdapter.setDeleteMode(!selectedAdapter.isDeleteMode());
                selectedAdapter.notifyDataSetChanged();
            });
            mBinding.rvSelected.setAdapter(selectedAdapter);
        }
        else {
            selectedAdapter.setList(list);
            selectedAdapter.notifyDataSetChanged();
        }
        if (list.size() == 0) {
            selectedAdapter.setDeleteMode(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_STAR ) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(StarSelectorActivity.RESP_SELECT_RESULT);
                mModel.setCandidates(list);
            }
        }
    }
}
