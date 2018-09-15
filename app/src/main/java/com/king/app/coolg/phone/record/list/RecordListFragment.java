package com.king.app.coolg.phone.record.list;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentRecordListBinding;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:44
 */
public class RecordListFragment extends MvvmFragment<FragmentRecordListBinding, RecordListViewModel> {

    public static final String ARG_RECORD_TYPE = "record_type";
    public static final String ARG_RECORD_SCENE = "record_scene";

    public static RecordListFragment newInstance(int type, String scene) {

        RecordListFragment fragment = new RecordListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RECORD_TYPE, type);
        bundle.putString(ARG_RECORD_SCENE, scene);
        fragment.setArguments(bundle);
        return fragment;
    }

    private IRecordListHolder holder;
    private RecordListAdapter listAdapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IRecordListHolder) holder;
    }

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
        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        mBinding.rvItems.setEnableLoadMore(true);
        mBinding.rvItems.setOnLoadMoreListener(() -> {
            // showCanBePlayed情况下已加载全部
            if (!mModel.isShowCanBePlayed()) {
                loadMoreRecords();
            }
        });

        mBinding.fabTop.setOnClickListener(v -> mBinding.rvItems.scrollToPosition(0));
    }

    @Override
    protected void onCreateData() {

        mModel.setRecordType(getArguments().getInt(ARG_RECORD_TYPE));
        mModel.setKeyScene(getArguments().getString(ARG_RECORD_SCENE));

        mModel.recordsObserver.observe(this, list -> showList(list));
        mModel.moreObserver.observe(this, offset -> showMoreList(offset));
        mModel.countObserver.observe(this, count -> holder.updateCount(mModel.getRecordType(), count));
        mModel.scrollPositionObserver.observe(this, offset -> mBinding.rvItems.scrollToPosition(offset));

        // 加载records
        loadNewRecords();
    }

    private void showList(List<RecordProxy> list) {
        if (listAdapter == null) {
            listAdapter = new RecordListAdapter();
            listAdapter.setList(list);
            listAdapter.setSortMode(mModel.getSortMode());
            listAdapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            mBinding.rvItems.setAdapter(listAdapter);
        }
        else {
            listAdapter.setList(list);
            listAdapter.setSortMode(mModel.getSortMode());
            listAdapter.notifyDataSetChanged();
        }
        mBinding.rvItems.scrollToPosition(0);

        mBinding.tvFilter.setText(mModel.getBottomText());
    }

    private void showMoreList(int offset) {
        listAdapter.notifyItemInserted(offset);
    }

    private void goToRecordPage(Record data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
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

    public void showCanPlayList(boolean canPlay) {
        mModel.setShowCanBePlayed(canPlay);
        loadNewRecords();
    }

    public void onSortChanged() {
        mModel.onSortTypeChanged();
        loadNewRecords();
    }

    public void onFilterChanged(RecordListFilterBean filter) {
        mModel.setFilter(filter);
        loadNewRecords();
    }

    public void onKeywordChanged(String keyword) {
        mModel.setKeyword(keyword);
        loadNewRecords();
    }

    public void setOffset(int offset) {
        if (offset < mModel.getOffset()) {
            mBinding.rvItems.scrollToPosition(offset);
        }
        else {
            mModel.setOffset(offset);
        }
    }
}
