package com.king.app.coolg.phone.record.tag;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.RecordActivity;
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
import com.king.app.jactionbar.JActionbar;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/10 11:12
 */
public abstract class AbsTagRecordActivity<T extends ViewDataBinding> extends MvvmActivity<T, TagRecordViewModel> {

    protected final int REQUEST_VIDEO_ORDER = 1603;
    private RecordGridAdapter recordAdapter;

    @Override
    protected TagRecordViewModel createViewModel() {
        return ViewModelProviders.of(this).get(TagRecordViewModel.class);
    }

    protected abstract RecyclerView getRecordRecyclerView();

    protected abstract void showTags(List<Tag> tags);

    protected abstract void focusOnTag(Integer position);

    protected abstract void goToClassicPage();

    protected abstract void goToRecordPage(Record record);

    protected abstract void addToPlayOrder(Record data);

    @Override
    protected void initData() {
        mModel.tagsObserver.observe(this, tags -> showTags(tags));
        mModel.recordsObserver.observe(this, list -> showRecords(list));
        mModel.moreObserver.observe(this, offset -> showMoreList(offset));
        mModel.scrollPositionObserver.observe(this, offset -> getRecordRecyclerView().scrollToPosition(offset));
        mModel.focusTagPosition.observe(this, position -> focusOnTag(position));

        mModel.loadTags();
    }

    protected void initActionBar(JActionbar actionbar) {
        actionbar.setOnBackListener(() -> onBackPressed());
        actionbar.setOnMenuItemListener(menuId -> {
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
                case R.id.menu_tag_sort_mode:
                    setTagSortMode();
                    break;
            }
        });

    }

    private void setTagSortMode() {
        new AlertDialogFragment()
                .setTitle(null)
                .setItems(AppConstants.TAG_SORT_MODE_TEXT, (dialog, which) -> {
                    SettingProperty.setTagSortType(which);
                    mModel.onTagSortChanged();
                    mModel.startSortTag(false);
                }).show(getSupportFragmentManager(), "AlertDialogFragment");
    }

    private void showRecords(List<RecordProxy> list) {
        if (recordAdapter == null) {
            recordAdapter = new RecordGridAdapter();
            recordAdapter.setList(list);
            recordAdapter.setPopupListener((view, position, record) -> showEditPopup(view, record));
            recordAdapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            getRecordRecyclerView().setAdapter(recordAdapter);
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
                    getRecordRecyclerView().scrollToPosition(offset);
                }
                else {
                    mModel.setOffset(offset);
                }
            } catch (Exception e) {}
        });
    }

}
