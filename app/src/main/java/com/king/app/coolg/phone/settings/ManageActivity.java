package com.king.app.coolg.phone.settings;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;

import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityManageBinding;
import com.king.app.coolg.model.bean.CheckDownloadBean;
import com.king.app.coolg.model.bean.DownloadDialogBean;
import com.king.app.coolg.model.http.bean.data.DownloadItem;
import com.king.app.coolg.model.http.bean.response.AppCheckBean;
import com.king.app.coolg.model.service.FileService;
import com.king.app.coolg.model.setting.ManageViewModel;
import com.king.app.coolg.phone.download.DownloadFragment;
import com.king.app.coolg.phone.download.OnDownloadListener;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/7 11:04
 */
@Route("Manage")
public class ManageActivity extends MvvmActivity<ActivityManageBinding, ManageViewModel> {

    @Override
    protected int getContentView() {
        return R.layout.activity_manage;
    }

    @Override
    protected void initView() {
        mBinding.groupSyncRating.setOnClickListener(v -> warningSyncRating());

        mBinding.groupClearImages.setOnClickListener(v -> {
            showMessageLong("Run on background...");
            startService(new Intent().setClass(ManageActivity.this, FileService.class));
        });
    }

    @Override
    protected ManageViewModel createViewModel() {
        return ViewModelProviders.of(this).get(ManageViewModel.class);
    }

    @Override
    protected void initData() {
        mBinding.setModel(mModel);

        mModel.imagesObserver.observe(this, bean -> imagesFound(bean));

        mModel.gdbCheckObserver.observe(this, bean -> gdbFound(bean));
        mModel.readyToDownloadObserver.observe(this, bean -> downloadDatabase(bean));
    }

    private void warningSyncRating() {
        new AlertDialogFragment()
                .setMessage("Synchronization will remove all star rating data in database. Please make sure you have uploaded changed data")
                .setPositiveText(getString(R.string.ok))
                .setPositiveListener((dialogInterface, i) -> mModel.syncRatings())
                .setNegativeText(getString(R.string.cancel))
                .show(getSupportFragmentManager(), "AlertDialogFragmentV4");
    }

    private void imagesFound(DownloadDialogBean bean) {
        DownloadFragment content = new DownloadFragment();
        content.setBean(bean);
        content.setOnDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadFinish(DownloadItem item) {

            }

            @Override
            public void onDownloadFinish() {
                showMessageLong(getString(R.string.gdb_download_done));
            }
        });
        DraggableDialogFragment fragment = new DraggableDialogFragment();
        fragment.setContentFragment(content);
        fragment.setTitle("Download");
        fragment.setMaxHeight(ScreenUtils.getScreenHeight());
        fragment.show(getSupportFragmentManager(), "DownloadFragment");
    }

    private void gdbFound(AppCheckBean bean) {
        String msg = String.format(getString(R.string.gdb_update_found), bean.getGdbDabaseVersion());
        new SimpleDialogs().showWarningActionDialog(this, msg
                , getResources().getString(R.string.yes)
                , null
                , getResources().getString(R.string.no)
                , (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        mModel.saveDataFromLocal(bean);
                    }
                });
    }

    private void downloadDatabase(AppCheckBean bean) {
        DownloadFragment content = new DownloadFragment();
        content.setBean(mModel.getDownloadDatabaseBean(bean));
        content.setOnDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadFinish(DownloadItem item) {
                mModel.databaseDownloaded();
            }

            @Override
            public void onDownloadFinish() {

            }
        });
        DraggableDialogFragment fragment = new DraggableDialogFragment();
        fragment.setContentFragment(content);
        fragment.setTitle("Download");
        fragment.setMaxHeight(ScreenUtils.getScreenHeight());
        fragment.show(getSupportFragmentManager(), "DownloadFragment");
    }
}
