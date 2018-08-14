package com.king.app.coolg.phone.order.record;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.king.app.coolg.phone.order.OrderFragment;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.gdb.data.entity.FavorRecordOrder;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 13:24
 */
public class RecordOrderFragment extends OrderFragment<RecordOrderViewModel, FavorRecordOrder> {

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
        return mModel.getCheckMap();
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
    public void deleteSelectedItems() {
        mModel.deleteSelectedItems();
        mModel.loadOrders();
    }
}
