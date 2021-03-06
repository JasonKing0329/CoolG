package com.king.app.coolg.pad.record.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.chenenyu.router.Router;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.phone.order.OrderPhoneActivity;
import com.king.app.coolg.phone.record.list.BaseRecordListFragment;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.view.widget.AutoLoadMoreRecyclerView;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 15:32
 */
public class RecordListPadFragment extends BaseRecordListFragment<RecordGridPadAdapter> {

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

    public static RecordListPadFragment newOrderInstance(long orderId) {

        RecordListPadFragment fragment = new RecordListPadFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_ORDER_ID, orderId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

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
    protected void updateRecordCount(Integer recordType, Integer count) {

    }

    @Override
    protected void showList(List<RecordProxy> list) {
        if (adapter == null) {
            adapter = new RecordGridPadAdapter();
            adapter.setList(list);
            adapter.setSortMode(mModel.getSortMode());
            adapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            adapter.setPopupListener((view, position, data) -> showEditPopup(view, data));
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

    @Override
    protected void selectOrderToAddRecord(Record data) {
        mModel.markRecordToAdd(data);
        Router.build("OrderPhone")
                .with(OrderPhoneActivity.EXTRA_SELECT_MODE, true)
                .with(OrderPhoneActivity.EXTRA_SELECT_RECORD, true)
                .requestCode(REQUEST_ADD_ORDER)
                .go(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果收不到回调，检查所在Activity是否实现了onActivityResult并且没有执行super.onActivityResult
        if (requestCode == REQUEST_ADD_ORDER) {
            if (resultCode == Activity.RESULT_OK) {
                long orderId = data.getLongExtra(AppConstants.RESP_ORDER_ID, -1);
                mModel.addToOrder(orderId);
            }
        }
    }

}
