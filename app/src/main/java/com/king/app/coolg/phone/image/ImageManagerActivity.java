package com.king.app.coolg.phone.image;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.base.adapter.BaseRecyclerAdapter;
import com.king.app.coolg.databinding.ActivityImageManagerBinding;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.jactionbar.OnCancelListener;
import com.king.app.jactionbar.OnConfirmListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/4 9:41
 */
@Route("ImageManager")
public class ImageManagerActivity extends MvvmActivity<ActivityImageManagerBinding, ImageViewModel> {

    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_DATA = "data";

    public static final String TYPE_STAR = "type_star";
    public static final String TYPE_RECORD = "type_record";

    private final int REQUEST_SET_VIDEO_COVER = 101;

    private StaggerAdapter staggerAdapter;

    @Override
    protected ImageViewModel createViewModel() {
        return generateViewModel(ImageViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_image_manager;
    }

    @Override
    protected void initView() {

        mBinding.setModel(mModel);

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mBinding.rvList.setLayoutManager(manager);

        mBinding.actionbar.setOnSelectAllListener(select -> {
            mModel.onSelectAll(select);
            return true;
        });
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_delete:
                    if (staggerAdapter != null) {
                        staggerAdapter.setSelectMode(true);
                        mBinding.actionbar.showConfirmStatus(menuId);
                        mBinding.actionbar.showSelectAll(true);
                    }
                    break;
            }
        });
        mBinding.actionbar.setOnConfirmListener(new OnConfirmListener() {
            @Override
            public boolean onConfirm(int actionId) {
                showConfirmCancelMessage("Are you sure to delete those images?"
                        , new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mModel.deleteSelectedItems();
                                mBinding.actionbar.showSelectAll(false);
                                mBinding.actionbar.cancelConfirmStatus();
                                staggerAdapter.setSelectMode(false);
                            }
                        }
                        , null);
                return false;
            }
        });
        mBinding.actionbar.setOnCancelListener(new OnCancelListener() {
            @Override
            public boolean onCancel(int actionId) {
                mBinding.actionbar.showSelectAll(false);
                staggerAdapter.setSelectMode(false);
                return true;
            }
        });
    }

    @Override
    protected void initData() {

        mModel.imageList.observe(this, list -> {
//            if (isStaggerView) {
                showStaggerList(list);
//            }
//            else {
//                showGridList(list);
//            }
        });

        dispatchIntent(getIntent());
    }

    private void dispatchIntent(Intent intent) {
        String type = intent.getStringExtra(EXTRA_TYPE);
        if (TYPE_STAR.equals(type)) {
            mModel.loadStarImages(intent.getLongExtra(EXTRA_DATA, -1));
        }
        else if (TYPE_RECORD.equals(type)) {
            mModel.loadRecordImages(intent.getLongExtra(EXTRA_DATA, -1));
        }
    }

    private void showStaggerList(List<ImageBean> list) {
        if (staggerAdapter == null) {
            staggerAdapter = new StaggerAdapter();
            staggerAdapter.setList(list);
            staggerAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<ImageBean>() {
                @Override
                public void onClickItem(View view, int position, ImageBean data) {
                    onApplyImage(data.getUrl());
                }
            });
            mBinding.rvList.setAdapter(staggerAdapter);
        }
        else {
            staggerAdapter.setList(list);
            staggerAdapter.notifyDataSetChanged();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SET_VIDEO_COVER) {
            if (resultCode == RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.setPlayOrderCover(list);
            }
        }
    }
}
