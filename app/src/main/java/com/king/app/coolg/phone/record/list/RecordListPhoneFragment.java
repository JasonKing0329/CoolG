package com.king.app.coolg.phone.record.list;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.view.widget.AutoLoadMoreRecyclerView;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 15:55
 */
public class RecordListPhoneFragment extends BaseRecordListFragment<RecordListAdapter> {

    private IRecordListHolder holder;

    public static RecordListPhoneFragment newInstance(int type, String scene) {

        RecordListPhoneFragment fragment = new RecordListPhoneFragment();
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
        rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected int getDefaultLoadNumber() {
        return 20;
    }

    @Override
    protected void updateRecordCount(Integer recordType, Integer count) {
        holder.updateCount(recordType, count);
    }

    @Override
    protected void showList(List<RecordProxy> list) {
        if (adapter == null) {
            adapter = new RecordListAdapter();
            adapter.setList(list);
            adapter.setSortMode(mModel.getSortMode());
            adapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
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
