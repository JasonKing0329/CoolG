package com.king.app.coolg.phone.video.order;

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
import com.king.app.coolg.databinding.ActivityPlayOrderBinding;
import com.king.app.coolg.phone.video.list.PlayListActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.jactionbar.OnConfirmListener;

@Route("PlayOrder")
public class PlayOrderActivity extends MvvmActivity<ActivityPlayOrderBinding, PlayOrderViewModel> {

    public static final String EXTRA_MULTI_SELECT = "select_multi";

    public static final String RESP_SELECT_RESULT = "select_result";

    private final int ACTION_MULTI_SELECT = 11;

    private final int REQUEST_PLAY_LIST = 6010;

    private PlayOrderAdapter adapter;

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

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mBinding.rvList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = ScreenUtils.dp2px(8);
            }
        });

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_add:
                    new SimpleDialogs().openInputDialog(PlayOrderActivity.this, "Create new play order", name -> {
                        mModel.addPlayOrder(name);
                    });
                    break;
                case R.id.menu_delete:
                    mBinding.actionbar.showConfirmStatus(menuId);
                    adapter.setMultiSelect(true);
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
                                });
                        break;
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
                adapter.setOnItemClickListener((view, position, data) -> Router.build("PlayList")
                        .with(PlayListActivity.EXTRA_ORDER_ID, data.getPlayOrder().getId())
                        .requestCode(REQUEST_PLAY_LIST)
                        .go(PlayOrderActivity.this));
                mBinding.rvList.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });

        mModel.loadOrders();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLAY_LIST) {
            if (resultCode == RESULT_OK) {

            }
        }
    }
}
