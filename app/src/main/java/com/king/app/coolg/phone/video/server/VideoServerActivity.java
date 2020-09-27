package com.king.app.coolg.phone.video.server;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.PopupMenu;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityVideoServerBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.phone.video.player.PlayerActivity;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.UrlUtil;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/11/11 9:11
 */
@Route("VideoServer")
public class VideoServerActivity extends MvvmActivity<ActivityVideoServerBinding, VideoServerViewModel> {

    private FileAdapter adapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_video_server;
    }

    @Override
    protected void initView() {
        mBinding.setModel(mModel);
        mBinding.tvUpper.setOnClickListener(v -> mModel.goUpper());

        mBinding.actionbar.setOnBackListener(() -> super.onBackPressed());
        mBinding.actionbar.setOnSearchListener(text -> mModel.onFilterChanged(text));
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_refresh:
                    mModel.refresh();
                    break;
            }
        });
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort);
        mBinding.actionbar.setPopupMenuProvider((iconMenuId, anchorView) -> {
            switch (iconMenuId) {
                case R.id.menu_sort:
                    return getSortPopup(anchorView);
            }
            return null;
        });

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private PopupMenu getSortPopup(View anchorView) {
        PopupMenu menu = new PopupMenu(this, anchorView);
        menu.getMenuInflater().inflate(R.menu.sort_video_server, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_sort_by_name:
                    mModel.onSortTypeChanged(PreferenceValue.VIDEO_SERVER_SORT_NAME);
                    break;
                case R.id.menu_sort_by_date:
                    mModel.onSortTypeChanged(PreferenceValue.VIDEO_SERVER_SORT_DATE);
                    break;
                case R.id.menu_sort_by_size:
                    mModel.onSortTypeChanged(PreferenceValue.VIDEO_SERVER_SORT_SIZE);
                    break;
            }
            return true;
        });
        return menu;
    }

    @Override
    public void onBackPressed() {
        if (mModel.backFolder()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected VideoServerViewModel createViewModel() {
        return ViewModelProviders.of(this).get(VideoServerViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.listObserver.observe(this, list -> {
            if (adapter == null) {
                adapter = new FileAdapter();
                adapter.setList(list);
                adapter.setOnItemClickListener((view, position, data) -> {
                    if (data.isFolder()) {
                        // clear filter when first tap it
                        mModel.clearFilter();
                        mModel.loadNewFolder(data);
                    }
                    else {
                        try {
                            String url = UrlUtil.toVideoUrl(data.getSourceUrl());
                            DebugLog.e("playUrl " + url);
                            mModel.createPlayList(url);
                            playUrl();
                        } catch (Exception e) {
                            showMessageShort("Unavailable url");
                        }
                    }
                });
                adapter.setOnActionListener(bean -> mModel.openFile(bean));
                mBinding.rvList.setAdapter(adapter);
            }
            else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }
        });

        mModel.loadNewFolder(null);
    }

    private void playUrl() {
        Router.build("Player")
                .go(this);
    }

}
