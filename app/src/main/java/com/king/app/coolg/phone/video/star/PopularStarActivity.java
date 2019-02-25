package com.king.app.coolg.phone.video.star;

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
import com.king.app.coolg.databinding.ActivityVideoStarListBinding;
import com.king.app.coolg.phone.star.list.StarSelectorActivity;
import com.king.app.coolg.phone.video.list.PlayStarListActivity;
import com.king.app.coolg.utils.ScreenUtils;
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
                    Router.build("StarSelector")
                            .requestCode(REQUEST_SELECT_STAR)
                            .go(PopularStarActivity.this);
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
