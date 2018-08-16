package com.king.app.coolg.phone.order.record;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.phone.order.OrderFragment;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.list.RecordListAdapter;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 13:24
 */
public class RecordOrderFragment extends OrderFragment<RecordOrderViewModel, FavorRecordOrder> {

    private RecordListAdapter recordAdapter;

    @Override
    protected void initItemsRecyclerView() {
        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mModel.recordItemsObserver.observe(this, list -> {
            if (recordAdapter == null) {
                recordAdapter = new RecordListAdapter();
                recordAdapter.setList(list);
                recordAdapter.setCheckMap(mModel.getItemCheckMap());
                recordAdapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data));
                mBinding.rvItems.setAdapter(recordAdapter);
            }
            else {
                recordAdapter.setList(list);
                recordAdapter.notifyDataSetChanged();
            }
        });
    }

    private void goToRecordPage(RecordProxy data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getRecord().getId())
                .go(this);
    }

    @Override
    protected RecordOrderViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordOrderViewModel.class);
    }

    @Override
    protected void onCreateData() {

        mModel.orderObserver.observe(this, list -> showOrders(list));

        mModel.loadOrders();
    }

    @Override
    protected void backToDepth(int index) {
        mModel.backToDepth(index);
    }

    @Override
    protected Map<Long, Boolean> getCheckMap() {
        return mModel.getOrderCheckMap();
    }

    @Override
    protected void loadFromOrder(OrderItem<FavorRecordOrder> data) {
        mModel.loadOrdersFrom(data.getBean());
    }

    @Override
    protected void onAddOrder(String name) {
        mModel.addNewOrder(name);
        mModel.loadOrders();
    }

    @Override
    protected void onAddChildOrder(View view, int position, OrderItem<FavorRecordOrder> data, String name) {
        mModel.addNewOrder(name, data.getBean().getId());
        mModel.loadOrders();
    }

    @Override
    protected void onEditOrder(View view, int position, OrderItem<FavorRecordOrder> data, String name) {
        mModel.editOrder(data.getBean(), name);
        data.setName(name);
        orderAdapter.notifyItemChanged(position);
    }

    @Override
    protected void onDeleteOrder(View view, int position, OrderItem<FavorRecordOrder> data) {
        mModel.deleteOrder(data.getId());
        mModel.loadOrders();
    }

    @Override
    protected void backToParent() {
        mModel.backToParent();
    }

    @Override
    protected void setItemSelectionMode(boolean selectionMode) {
        if (recordAdapter != null) {
            recordAdapter.setSelectionMode(selectionMode);
            recordAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteSelectedItems() {
        if (mBinding.rvItems.getVisibility() == View.VISIBLE) {
            mModel.deleteSelectedItems(true);
            mModel.loadRecordItems();
        }
        else {
            mModel.deleteSelectedItems(false);
            mModel.loadOrders();
        }
    }

    @Override
    public void onSortTypeChanged() {
        mModel.onSortTypeChanged();
    }

    @Override
    protected void updateOrderCover(OrderItem<FavorRecordOrder> data, String coverPath) {
        mModel.updateCover(data.getBean(), coverPath);
    }

    @Override
    protected void loadOrderItems(OrderItem<FavorRecordOrder> data) {
        mModel.loadRecordItems(data.getBean());
    }
}
