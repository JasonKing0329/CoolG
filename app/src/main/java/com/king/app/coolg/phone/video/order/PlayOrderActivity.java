package com.king.app.coolg.phone.video.order;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
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
import com.king.app.coolg.databinding.ActivityPlayOrderBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.coolg.phone.video.list.PlayListActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.jactionbar.OnConfirmListener;

@Route("PlayOrder")
public class PlayOrderActivity extends MvvmActivity<ActivityPlayOrderBinding, PlayOrderViewModel> {

    public static final String EXTRA_MULTI_SELECT = "select_multi";

    public static final String RESP_SELECT_RESULT = "select_result";

    private final int ACTION_MULTI_SELECT = 11;

    private final int REQUEST_PLAY_LIST = 6010;

    private PlayOrderAdapter adapter;

    private boolean isEditMode;

    @Override
    protected PlayOrderViewModel createViewModel() {
        return ViewModelProviders.of(this).get(PlayOrderViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_play_order;
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
                    new SimpleDialogs().openInputDialog(PlayOrderActivity.this, "Create new play order", name -> {
                        mModel.addPlayOrder(name);
                    });
                    break;
                case R.id.menu_edit:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    isEditMode = true;
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    adapter.setMultiSelect(true);
                    break;
                case R.id.menu_sort:
                    new AlertDialogFragment()
                            .setItems(getResources().getStringArray(R.array.sort_play_order), (dialog, which) -> {
                                if (which == 0) {
                                    mModel.sortById();
                                }
                                else if (which == 1) {
                                    mModel.sortByName();
                                }
                            })
                            .show(getSupportFragmentManager(), "AlertDialogFragment");
                    break;
                case R.id.menu_list_view_type:
                    int type = SettingProperty.getVideoPlayOrderViewType();
                    if (type == PreferenceValue.VIEW_TYPE_GRID) {
                        type = PreferenceValue.VIEW_TYPE_LIST;
                    }
                    else {
                        type = PreferenceValue.VIEW_TYPE_GRID;
                    }
                    SettingProperty.setVideoPlayOrderViewType(type);
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
                        new SimpleDialogs().showWarningActionDialog(PlayOrderActivity.this
                                , "Delete order will delete related items, continue?"
                                , "Yes", null
                                , (dialogInterface, i) -> {
                                    mModel.executeDelete();
                                    adapter.setMultiSelect(false);
                                    mBinding.actionbar.cancelConfirmStatus();
                                    setResultChanged();
                                });
                        break;
                    case R.id.menu_edit:
                        isEditMode = false;
                        return true;
                    case ACTION_MULTI_SELECT:
                        Intent intent = new Intent();
                        intent.putCharSequenceArrayListExtra(RESP_SELECT_RESULT, mModel.getSelectedItems());
                        setResult(RESULT_OK, intent);
                        finish();
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
                    case R.id.menu_edit:
                        isEditMode = false;
                        break;
                    case ACTION_MULTI_SELECT:
                        finish();
                        break;
                }
                return true;
            }
        });

        if (isMultiSelect()) {
            mBinding.actionbar.showConfirmStatus(ACTION_MULTI_SELECT);
        }
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
        int type = SettingProperty.getVideoPlayOrderViewType();
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

    private boolean isMultiSelect() {
        return getIntent().getBooleanExtra(EXTRA_MULTI_SELECT, false);
    }

    @Override
    protected void initData() {
        mModel.dataObserver.observe(this, list -> {
            if (adapter == null) {
                adapter = new PlayOrderAdapter();
                adapter.setList(list);
                adapter.setMultiSelect(isMultiSelect());
                adapter.setOnItemClickListener((view, position, data) -> {
                    if (isEditMode) {
                        updateOrderName(position, data);
                    }
                    else {
                        Router.build("PlayList")
                                .with(PlayListActivity.EXTRA_ORDER_ID, data.getPlayOrder().getId())
                                .requestCode(REQUEST_PLAY_LIST)
                                .go(PlayOrderActivity.this);
                    }
                });
                mBinding.rvList.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });

        mModel.loadOrders();
    }

    private void updateOrderName(int position, VideoPlayList data) {
        new SimpleDialogs().openInputDialog(this, "Rename", data.getName()
            , name -> {
                    mModel.updateOrderName(data, name);
                    adapter.notifyItemChanged(position);
                    setResultChanged();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLAY_LIST) {
            if (resultCode == RESULT_OK) {
                mModel.loadOrders();
            }
        }
    }

    public void setResultChanged() {
        setResult(RESULT_OK);
    }
}
