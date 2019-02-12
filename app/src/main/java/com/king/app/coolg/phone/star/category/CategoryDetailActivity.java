package com.king.app.coolg.phone.star.category;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.base.adapter.TwoTypeBindingAdapter;
import com.king.app.coolg.databinding.ActivityCategoryDetailBinding;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.list.StarSelectorActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.jactionbar.OnConfirmListener;

import java.util.ArrayList;

@Route("CategoryDetail")
public class CategoryDetailActivity extends MvvmActivity<ActivityCategoryDetailBinding, CategoryDetailViewModel> {

    public static final String EXTRA_CATEGORY_ID = "category_id";

    public static final int REQUEST_SELECT_STAR = 501;

    private CandidateAdapter candidateAdapter;
    private LevelAdapter levelAdapter;

    @Override
    protected CategoryDetailViewModel createViewModel() {
        return ViewModelProviders.of(this).get(CategoryDetailViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_category_detail;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_edit:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    mModel.setEditMode(true);
                    candidateAdapter.setDeleteMode(false);
                    candidateAdapter.notifyDataSetChanged();
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return true;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return true;
            }

            @Override
            public boolean onConfirm(int actionId) {
                mModel.saveCategoryDetail();
                return false;
            }

            @Override
            public boolean onCancel(int actionId) {
                showConfirmCancelMessage("Modification will not be saved, continue?"
                        , (dialogInterface, i) -> {
                            mModel.setEditMode(false);
                            mModel.cancelEdit();
                            reload();
                        }, null);
                return true;
            }
        });

        mBinding.rvCandidate.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvCandidate.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(8);
            }
        });
        mBinding.rvDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void initData() {
        mModel.candidatesObserver.observe(this, list -> {
            if (candidateAdapter == null) {
                candidateAdapter = new CandidateAdapter();
                candidateAdapter.setList(list);
                candidateAdapter.setOnItemClickListener(new TwoTypeBindingAdapter.OnItemClickListener<CategoryStar, CategoryAdd>() {
                    @Override
                    public void onClickType1(View view, int position, CategoryStar data) {
                        if (mModel.isEditMode()) {
                            data.getObserver().onSelect(data);
                        }
                        else {
                            goToStarPage(data.getStar().getStarId());
                        }
                    }

                    @Override
                    public void onClickType2(View view, int position, CategoryAdd data) {
                        if (mModel.isEditMode()) {
                            showMessageLong("Please save data before add new candidates");
                            return;
                        }
                        addCandidates();
                    }
                });
                candidateAdapter.setOnItemLongClickListener(new TwoTypeBindingAdapter.OnItemLongClickListener<CategoryStar, CategoryAdd>() {
                    @Override
                    public void onLongClickType1(View view, int position, CategoryStar data) {
                        if (mModel.isEditMode()) {
                            showMessageLong("Please save data before edit candidate");
                            return;
                        }
                        if (candidateAdapter.isDeleteMode()) {
                            candidateAdapter.setDeleteMode(false);
                        }
                        else {
                            candidateAdapter.setDeleteMode(true);
                        }
                        candidateAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onLongClickType2(View view, int position, CategoryAdd data) {
                    }
                });
                candidateAdapter.setOnDeleteListener((position, star) -> {
                    mModel.deleteCandidates(star);
                    candidateAdapter.notifyDataSetChanged();
                });
                mBinding.rvCandidate.setAdapter(candidateAdapter);
            }
            else {
                candidateAdapter.setList(list);
                candidateAdapter.notifyDataSetChanged();
            }
        });

        mModel.levelsObserver.observe(this, list -> {
            if (levelAdapter == null) {
                levelAdapter = new LevelAdapter();
                levelAdapter.setList(list);
                levelAdapter.setOnLevelListener(new LevelAdapter.OnLevelListener() {
                    @Override
                    public void onInsertLevel(CategoryLevel level) {
                        addLevel(level);
                    }

                    @Override
                    public void onRemoveLevel(CategoryLevel level) {
                        mModel.removeLevel(level);
                    }

                    @Override
                    public void onSelectLevelStar(CategoryLevel level, CategoryStar star) {
                        if (mModel.isEditMode()) {
                            mModel.onSelectLevelStar(level, star);
                        }
                        else {
                            goToStarPage(star.getStar().getStarId());
                        }
                    }
                });
                mBinding.rvDetails.setAdapter(levelAdapter);
            }
            else {
                levelAdapter.setList(list);
                levelAdapter.notifyDataSetChanged();
            }
        });

        mModel.insertLevelIndex.observe(this, index -> {
            levelAdapter.notifyItemRangeChanged(index, mModel.levelsObserver.getValue().size() - index);
            mBinding.rvDetails.smoothScrollToPosition(index);
        });

        mModel.removeLevelIndex.observe(this, index -> {
//            levelAdapter.notifyItemRangeChanged(index, mModel.levelsObserver.getValue().size() - index);
            levelAdapter.notifyDataSetChanged();
        });

        mModel.levelDataChanged.observe(this, level -> levelAdapter.notifyLevelChanged(level));

        mModel.candidateRemoved.observe(this, star -> candidateAdapter.notifyStarRemoved(star));

        mModel.cancelConfirmStatus.observe(this, cancel -> {
            mBinding.actionbar.cancelConfirmStatus();
            mModel.setEditMode(false);
        });

        reload();
    }

    private void reload() {
        long id = getIntent().getLongExtra(EXTRA_CATEGORY_ID, -1);
        mModel.loadCandidates(id);
        mModel.loadLevels();
    }

    private void addLevel(CategoryLevel level) {
        mModel.addNewLevelAfter(level);
    }

    private void addCandidates() {
        Router.build("StarSelector")
                .requestCode(REQUEST_SELECT_STAR)
                .go(this);
    }

    private void goToStarPage(long starId) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, starId)
                .go(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_STAR ) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(StarSelectorActivity.RESP_SELECT_RESULT);
                mModel.addCandidates(list);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mModel.isEditMode()) {
            showMessageLong("Please cancel or confirm edit first");
            return;
        }
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
