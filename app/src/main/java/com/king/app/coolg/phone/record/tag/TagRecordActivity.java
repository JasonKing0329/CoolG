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
public class TagRecordActivity extends MvvmActivity<ActivityRecordTagBinding, TagRecordViewModel> {

    protected final int REQUEST_VIDEO_ORDER = 1603;
    private TagAdapter tagAdapter;

    private RecordGridAdapter recordAdapter;

    @Override
    protected TagRecordViewModel createViewModel() {
        return ViewModelProviders.of(this).get(TagRecordViewModel.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_record_tag;
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

        mBinding.actionbar.setOnBackListener(() -> onBackPressed());
        mBinding.actionbar.setOnMenuItemListener(menuId -> {
            switch (menuId) {
                case R.id.menu_sort:
                    changeSortType();
                    break;
                case R.id.menu_filter:
                    changeFilter();
                    break;
                case R.id.menu_classic:
                    goToClassicPage();
                    break;
                case R.id.menu_offset:
                    showSetOffset();
                    break;
            }
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvRecords.scrollToPosition(0));
    }

    private void goToClassicPage() {
        Router.build("RecordListPhone")
                .go(this);
    }

    @Override
    protected void initData() {
        mModel.tagsObserver.observe(this, tags -> showTags(tags));
        mModel.recordsObserver.observe(this, list -> showRecords(list));
        mModel.moreObserver.observe(this, offset -> showMoreList(offset));
        mModel.scrollPositionObserver.observe(this, offset -> mBinding.rvRecords.scrollToPosition(offset));

        mModel.loadTags();
    }

    private void showTags(List<Tag> tags) {
        tagAdapter = new TagAdapter();
        tagAdapter.setList(tags);
        tagAdapter.setSelection(0);
        tagAdapter.setOnItemClickListener((view, position, data) -> mModel.loadTagRecords(data.getId()));
        mBinding.rvTags.setAdapter(tagAdapter);
    }

    private void showRecords(List<RecordProxy> list) {
        if (recordAdapter == null) {
            recordAdapter = new RecordGridAdapter();
            recordAdapter.setList(list);
            recordAdapter.setPopupListener((view, position, record) -> showEditPopup(view, record));
            recordAdapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            mBinding.rvRecords.setAdapter(recordAdapter);
        }
        else {
            recordAdapter.setList(list);
            recordAdapter.notifyDataSetChanged();
        }
    }

    protected void showEditPopup(View view, Record data) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.getMenuInflater().inflate(R.menu.popup_record_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.menu_set_cover).setVisible(false);
        menu.getMenu().findItem(R.id.menu_delete).setVisible(false);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_add_to_order:
//                    selectOrderToAddRecord(data);
                    break;
                case R.id.menu_add_to_play_order:
                    addToPlayOrder(data);
                    break;
            }
            return false;
        });
        menu.show();
    }

    private void addToPlayOrder(Record record) {
        mModel.saveRecordToPlayOrder(record);
        Router.build("PlayOrder")
                .with(PlayOrderActivity.EXTRA_MULTI_SELECT, true)
                .requestCode(REQUEST_VIDEO_ORDER)
                .go(this);
    }

    private void goToRecordPage(Record data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
    }

    private void showMoreList(int offset) {
        recordAdapter.notifyItemInserted(offset);
    }

    public void changeSortType() {
        SortDialogContent content = new SortDialogContent();
        content.setDesc(SettingProperty.isRecordSortDesc());
        content.setSortType(SettingProperty.getRecordSortType());
        content.setOnSortListener((desc, sortMode) -> {
            SettingProperty.setRecordSortType(sortMode);
            SettingProperty.setRecordSortDesc(desc);
            mModel.onSortTypeChanged();
            mModel.loadTagRecords();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setContentFragment(content);
        dialogFragment.setTitle("Sort");
        dialogFragment.show(getSupportFragmentManager(), "SortDialogContent");
    }

    public void changeFilter() {
        RecommendFragment content = new RecommendFragment();
        content.setBean(mModel.getFilter());
//        content.setFixedType(ftRecords.getCurrentItem());
        content.setOnRecommendListener(bean -> {
            mModel.setFilter(bean);
            mModel.loadTagRecords();
        });
        DraggableDialogFragment dialogFragment = new DraggableDialogFragment();
        dialogFragment.setTitle("Recommend Setting");
        dialogFragment.setContentFragment(content);
        dialogFragment.setMaxHeight(ScreenUtils.getScreenHeight() * 2 / 3);
        dialogFragment.show(getSupportFragmentManager(), "RecommendFragment");
    }

    public void showSetOffset() {
        new SimpleDialogs().openInputDialog(this, "set offset", name -> {
            try {
                int offset = Integer.parseInt(name);
                if (offset < mModel.getOffset()) {
                    mBinding.rvRecords.scrollToPosition(offset);
                }
                else {
                    mModel.setOffset(offset);
                }
            } catch (Exception e) {}
        });
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
