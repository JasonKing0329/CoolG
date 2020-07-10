package com.king.app.coolg.phone.record.tag;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.databinding.ActivityRecordTagBinding;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.TagAdapter;
import com.king.app.coolg.phone.record.list.RecordGridAdapter;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.record.list.SortDialogContent;
import com.king.app.coolg.phone.video.home.RecommendFragment;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.AlertDialogFragment;
import com.king.app.coolg.view.dialog.DraggableDialogFragment;
import com.king.app.coolg.view.dialog.SimpleDialogs;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/5 17:11
 */
@Route("TagRecord")
public class TagRecordActivity extends AbsTagRecordActivity<ActivityRecordTagBinding> {

    private TagAdapter tagAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_tag;
    }

    @Override
    protected RecyclerView getRecordRecyclerView() {
        return mBinding.rvRecords;
    }

    @Override
    protected void initView() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
        mBinding.rvTags.setLayoutManager(manager);
        mBinding.rvTags.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.left = ScreenUtils.dp2px(10);
                outRect.top = ScreenUtils.dp2px(5);
                outRect.bottom = ScreenUtils.dp2px(5);
            }
        });

        mBinding.rvRecords.setLayoutManager(new GridLayoutManager(this, 2));
        mBinding.rvRecords.setEnableLoadMore(true);
        mBinding.rvRecords.setOnLoadMoreListener(() -> mModel.loadMoreRecords(null));

        initActionBar(mBinding.actionbar);

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvRecords.scrollToPosition(0));
    }

    @Override
    protected void focusOnTag(Integer position) {
        tagAdapter.setSelection(position);
        tagAdapter.notifyDataSetChanged();
    }

    @Override
    protected void showTags(List<Tag> tags) {
        if (tagAdapter == null) {
            tagAdapter = new TagAdapter();
            tagAdapter.setList(tags);
            tagAdapter.setSelection(0);
            tagAdapter.setOnItemClickListener((view, position, data) -> mModel.loadTagRecords(data.getId()));
            mBinding.rvTags.setAdapter(tagAdapter);
        }
        else {
            tagAdapter.setList(tags);
            tagAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void goToClassicPage() {
        Router.build("RecordListPhone")
                .go(this);
    }

    @Override
    protected void goToRecordPage(Record data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
    }

    @Override
    protected void addToPlayOrder(Record record) {
        mModel.saveRecordToPlayOrder(record);
        Router.build("PlayOrder")
                .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                .requestCode(REQUEST_VIDEO_ORDER)
                .go(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_VIDEO_ORDER) {
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<CharSequence> list = data.getCharSequenceArrayListExtra(PlayOrderActivity.RESP_SELECT_RESULT);
                mModel.addToPlay(list);
            }
        }
    }
}
