package com.king.app.coolg.phone.star.list;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityStarSelectorBinding;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.widget.FitSideBar;
import com.king.app.jactionbar.OnConfirmListener;

@Route("StarSelector")
public class StarSelectorActivity extends MvvmActivity<ActivityStarSelectorBinding, StarSelectorViewModel> {

    public static final String RESP_SELECT_RESULT = "resp_select_result";

    public static final String EXTRA_SINGLE = "select_single";

    public static final String EXTRA_LIMIT_MAX = "select_limit_max";

    @Override
    protected StarSelectorViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarSelectorViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_star_selector;
    }

    @Override
    protected void initView() {
        if (ScreenUtils.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            mBinding.rvStar.setLayoutManager(new GridLayoutManager(this, 3));
            mBinding.rvStar.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildLayoutPosition(view);
                    outRect.top = ScreenUtils.dp2px(8);
                    if (position % 3 == 0) {
                        outRect.left = ScreenUtils.dp2px(16);
                    }
                    else if (position % 3 == 1) {
                        outRect.left = ScreenUtils.dp2px(16);
                    }
                    else {
                        outRect.left = ScreenUtils.dp2px(16);
                        outRect.right = ScreenUtils.dp2px(8);
                    }
                }
            });
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            mBinding.rvStar.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            mBinding.rvStar.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.top = ScreenUtils.dp2px(8);
                }
            });
        }

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.showConfirmStatus(0);
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean disableInstantDismissConfirm() {
                return false;
            }

            @Override
            public boolean disableInstantDismissCancel() {
                return false;
            }

            @Override
            public boolean onConfirm(int actionId) {
                setSelectResult();
                finish();
                return true;
            }

            @Override
            public boolean onCancel(int actionId) {
                finish();
                return true;
            }
        });

        mBinding.sidebar.setOnSidebarStatusListener(new FitSideBar.OnSidebarStatusListener() {
            @Override
            public void onChangeFinished() {
                mBinding.tvIndexPopup.setVisibility(View.GONE);
            }

            @Override
            public void onSideIndexChanged(String index) {
                int selection = mModel.getLetterPosition(index);
                scrollToPosition(selection);

                mBinding.tvIndexPopup.setText(index);
                mBinding.tvIndexPopup.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setSelectResult() {
        Intent intent = new Intent();
        intent.putCharSequenceArrayListExtra(RESP_SELECT_RESULT, mModel.getSelectedItems());
        setResult(RESULT_OK, intent);
    }

    private boolean isSingleSelect() {
        return getIntent().getBooleanExtra(EXTRA_SINGLE, false);
    }

    private int getLimitMax() {
        return getIntent().getIntExtra(EXTRA_LIMIT_MAX, 0);
    }

    @Override
    protected void initData() {

        mModel.starsObserver.observe(this, list -> {
            StarSelectorAdapter adapter = new StarSelectorAdapter();
            adapter.setList(list);
            mBinding.rvStar.setAdapter(adapter);
        });

        mModel.indexObserver.observe(this, index -> mBinding.sidebar.addIndex(index));
        mModel.indexBarObserver.observe(this, show -> {
            mBinding.sidebar.build();
            mBinding.sidebar.setVisibility(View.VISIBLE);
        });

        mModel.setSingleSelect(isSingleSelect());
        mModel.setLimitMax(getLimitMax());
        mModel.loadStars();
    }

    private void scrollToPosition(int selection) {
        LinearLayoutManager manager = (LinearLayoutManager) mBinding.rvStar.getLayoutManager();
        manager.scrollToPositionWithOffset(selection, 0);
    }

}
