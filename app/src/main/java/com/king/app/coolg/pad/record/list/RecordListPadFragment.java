package com.king.app.coolg.pad.record.list;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.pad.record.RecordGridPadAdapter;
import com.king.app.coolg.phone.record.list.BaseRecordListFragment;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.view.widget.AutoLoadMoreRecyclerView;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 15:32
 */
public class RecordListPadFragment extends BaseRecordListFragment<RecordGridPadAdapter> {

    private IRecordListHolder holder;

    public static RecordListPadFragment newInstance(long starId) {

        RecordListPadFragment fragment = new RecordListPadFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_STAR_ID, starId);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static RecordListPadFragment newInstance(int type, String scene) {

        RecordListPadFragment fragment = new RecordListPadFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_RECORD_TYPE, type);
        bundle.putString(ARG_RECORD_SCENE, scene);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IRecordListHolder) holder;
    }

    @Override
    protected void initRecyclerView(AutoLoadMoreRecyclerView rvItems) {
        rvItems.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    protected int getDefaultLoadNumber() {
        return 21;
    }

    @Override
    protected void updateRecordCount(int recordType, Integer count) {

    }

    @Override
    protected void showList(List<RecordProxy> list) {
        if (adapter == null) {
            adapter = new RecordGridPadAdapter();
            adapter.setList(list);
            adapter.setSortMode(mModel.getSortMode());
            adapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            adapter.setPopupListener((view, position, data) -> holder.showRecordPopup(view, data));
            mBinding.rvItems.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.setSortMode(mModel.getSortMode());
            adapter.notifyDataSetChanged();
        }
        mBinding.rvItems.scrollToPosition(0);

        mBinding.tvFilter.setText(mModel.getBottomText());
    }
}
