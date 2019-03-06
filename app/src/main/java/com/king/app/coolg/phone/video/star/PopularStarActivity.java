package com.king.app.coolg.phone.video.star;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoStarListBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.list.StarSelectorActivity;
import com.king.app.coolg.phone.video.list.PlayStarListActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.jactionbar.OnConfirmListener;

import java.util.ArrayList;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/25 13:40
 */
@Route("PopularStar")
public class PopularStarActivity extends MvvmActivity<ActivityVideoStarListBinding, PopularStarViewModel> {

    public static final int REQUEST_SELECT_STAR = 6101;
    private PopularStarAdapter adapter;

    @Override
    protected PopularStarViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PopularStarViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_star_list;
    }

    @Override
    protected void initView() {
        if (ScreenUtils.isTablet()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        updateListViewType();

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    Router.build("StarSelector")
                            .requestCode(REQUEST_SELECT_STAR)
                            .go(PopularStarActivity.this);
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    adapter.setMultiSelect(true);
                    break;
                case R.id.menu_sort:
                    new AlertDialogFragment()
                            .setItems(getResources().getStringArray(R.array.sort_video_star_order), (dialog, which) -> {
                                if (which == 0) {
                                    mModel.sortByName();
                                }
                                else if (which == 1) {
                                    mModel.sortByVideo();
                                }
                            })
                            .show(getSupportFragmentManager(), "AlertDialogFragment");
                    break;
                case R.id.menu_list_view_type:
                    int type = SettingProperty.getVideoStarOrderViewType();
                    if (type == PreferenceValue.VIEW_TYPE_GRID) {
                        type = PreferenceValue.VIEW_TYPE_LIST;
                    }
                    else {
                        type = PreferenceValue.VIEW_TYPE_GRID;
                    }
                    SettingProperty.setVideoStarOrderViewType(type);
                    updateListViewType();
                    break;
            }
        });
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
                switch (actionId) {
                    case R.id.menu_delete:
                        new SimpleDialogs().showWarningActionDialog(PopularStarActivity.this
                                , "Delete order will delete related items, continue?"
                                , "Yes", null
                                , (dialogInterface, i) -> {
                                    mModel.executeDelete();
                                    adapter.setMultiSelect(false);
                                    mBinding.actionbar.cancelConfirmStatus();
                                });
                        break;
                }
                return false;
            }

            @Override
            public boolean onCancel(int actionId) {
                switch (actionId) {
                    case R.id.menu_delete:
                        adapter.setMultiSelect(false);
                        break;
                }
                return true;
            }
        });

    }

    private RecyclerView.ItemDecoration linearDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.top = ScreenUtils.dp2px(8);
        }
    };

    private RecyclerView.ItemDecoration gridDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            outRect.top = ScreenUtils.dp2px(8);
            if (position % 2 == 0) {
                outRect.left = ScreenUtils.dp2px(8);
                outRect.right = ScreenUtils.dp2px(4);
            }
            else {
                outRect.left = ScreenUtils.dp2px(4);
                outRect.right = ScreenUtils.dp2px(8);
            }
        }
    };

    private RecyclerView.ItemDecoration gridTabDecoration = new RecyclerView.ItemDecoration() {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildLayoutPosition(view);
            if (position / 3 == 0) {
                outRect.top = ScreenUtils.dp2px(8);
            }
            else {
                outRect.top = 0;
            }
        }
    };

    private void updateListViewType() {
        int type = SettingProperty.getVideoStarOrderViewType();
        if (ScreenUtils.isTablet()) {
            type = PreferenceValue.VIEW_TYPE_GRID_TAB;
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
            mBinding.rvList.setLayoutManager(gridLayoutManager);
            mBinding.rvList.addItemDecoration(gridTabDecoration);
            mBinding.actionbar.updateMenuItemVisible(R.id.menu_list_view_type, false);
        }
        else {
            if (type == PreferenceValue.VIEW_TYPE_GRID) {
                mBinding.rvList.removeItemDecoration(linearDecoration);
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                mBinding.rvList.setLayoutManager(gridLayoutManager);
                mBinding.rvList.addItemDecoration(gridDecoration);
                mBinding.actionbar.updateMenuText(R.id.menu_list_view_type, "List View");
            }
            else {
                mBinding.rvList.removeItemDecoration(gridDecoration);
                mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
                mBinding.rvList.addItemDecoration(linearDecoration);
                mBinding.actionbar.updateMenuText(R.id.menu_list_view_type, "Grid View");
            }
        }

        if (adapter != null) {
            adapter.setViewType(type);
        }
    }

    @Override
    protected void initData() {
        mModel.starsObserver.observe(this, list -> {
            if (adapter == null) {
                adapter = new PopularStarAdapter();
                adapter.setList(list);
                adapter.setOnItemClickListener((view, position, data) -> Router.build("PlayStarList")
                        .with(PlayStarListActivity.EXTRA_STAR_ID, data.getStar().getId())
                        .go(PopularStarActivity.this));
                mBinding.rvList.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });

        mModel.loadStars();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_STAR) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(StarSelectorActivity.RESP_SELECT_RESULT);
                mModel.insertVideoCoverStar(list);
            }
        }
    }
}
