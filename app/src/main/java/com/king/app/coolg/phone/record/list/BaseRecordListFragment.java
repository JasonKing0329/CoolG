package com.king.app.coolg.phone.record.list;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentRecordListBinding;
import com.king.app.coolg.pad.record.RecordPadActivity;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.video.home.RecommendBean;
import com.king.app.coolg.phone.video.order.PlayOrderActivity;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.widget.AutoLoadMoreRecyclerView;
import com.king.app.gdb.data.entity.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 15:47
 */
public abstract class BaseRecordListFragment<T extends RecyclerView.Adapter> extends MvvmFragment<FragmentRecordListBinding, RecordListViewModel> {

    protected final int REQUEST_ADD_ORDER = 1602;
    protected final int REQUEST_VIDEO_ORDER = 1603;

    public static final String ARG_RECORD_TYPE = "record_type";
    public static final String ARG_RECORD_SCENE = "record_scene";
    public static final String ARG_STAR_ID = "star_id";
    public static final String ARG_ORDER_ID = "order_id";

    protected T adapter;

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_record_list;
    }

    @Override
    protected RecordListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordListViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        initRecyclerView(mBinding.rvItems);
        
        mBinding.rvItems.setEnableLoadMore(true);
        mBinding.rvItems.setOnLoadMoreListener(() -> {
            loadMoreRecords();
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvItems.scrollToPosition(0));
    }

    protected abstract void initRecyclerView(AutoLoadMoreRecyclerView rvItems);

    @Override
    protected void onCreateData() {

        mModel.setDefaultLoadNumber(getDefaultLoadNumber());
        mModel.setKeyScene(getArguments().getString(ARG_RECORD_SCENE));
        mModel.setStarId(getArguments().getLong(ARG_STAR_ID));
        mModel.setOrderId(getArguments().getLong(ARG_ORDER_ID));

        mModel.recordsObserver.observe(this, list -> showList(list));
        mModel.moreObserver.observe(this, offset -> showMoreList(offset));
        mModel.countObserver.observe(this, count -> updateRecordCount(mModel.getRecordType(), count));

        // 加载records
        loadNewRecords();
    }


    protected void showEditPopup(View view, Record data) {
        PopupMenu menu = new PopupMenu(getActivity(), view);
        menu.getMenuInflater().inflate(R.menu.popup_record_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.menu_set_cover).setVisible(false);
        menu.getMenu().findItem(R.id.menu_delete).setVisible(false);
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_add_to_order:
                    selectOrderToAddRecord(data);
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

    protected void selectOrderToAddRecord(Record data) {

    }

    protected abstract int getDefaultLoadNumber();

    protected abstract void updateRecordCount(Integer recordType, Integer count);

    protected abstract void showList(List<RecordProxy> list);

    private void showMoreList(int offset) {
        adapter.notifyItemInserted(offset);
    }

    protected void goToRecordPage(Record data) {
        if (ScreenUtils.isTablet()) {
            Router.build("RecordPad")
                    .with(RecordPadActivity.EXTRA_RECORD_ID, data.getId())
                    .go(this);
        }
        else {
            Router.build("RecordPhone")
                    .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                    .go(this);
        }
    }

    /**
     * 修改排序类型、关键词变化，重新加载list
     */
    public void loadNewRecords() {
        // 重新加载records
        mModel.loadRecordList();
    }

    /**
     * 不改变排序模式、不改变关键词，仅在滑动到底部后自动加载更多
     */
    private void loadMoreRecords() {
        // 加到当前size后
        mModel.loadMoreRecords();
    }

    public void onSceneChanged(String scene) {
        mModel.setKeyScene(scene);
        loadNewRecords();
    }

    public void onRecordTypeChanged(int type) {
        mModel.setRecordType(type);
        loadNewRecords();
    }

    public void onSortChanged() {
        mModel.onSortTypeChanged();
        loadNewRecords();
    }

    public void onStarRecordsSortChanged() {
        mModel.onStarRecordsSortTypeChanged();
        loadNewRecords();
    }

    public void onFilterChanged(RecommendBean filter) {
        mModel.setFilter(filter);
        loadNewRecords();
    }

    public void onKeywordChanged(String keyword) {
        mModel.setKeyword(keyword);
        loadNewRecords();
    }

    public void onStarChanged(long starId) {
        mModel.setStarId(starId);
        mModel.onStarRecordsSortTypeChanged();
        loadNewRecords();
    }

    public void filterByStarType(int starType) {
        mModel.setStarType(starType);
        loadNewRecords();
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
