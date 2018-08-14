package com.king.app.coolg.phone.order.star;

import android.arch.lifecycle.ViewModelProviders;
import android.view.View;

import com.king.app.coolg.phone.order.OrderFragment;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.gdb.data.entity.FavorStarOrder;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 13:24
 */
public class StarOrderFragment extends OrderFragment<StarOrderViewModel, FavorStarOrder> {

    @Override
    protected StarOrderViewModel createViewModel() {
        return ViewModelProviders.of(this).get(StarOrderViewModel.class);
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
    protected void loadFromOrder(OrderItem<FavorStarOrder> data) {
        mModel.loadOrdersFrom(data.getBean());
    }

    @Override
    protected void onAddOrder(String name) {
        mModel.addNewOrder(name);
        mModel.loadOrders();
    }

    @Override
    protected void onAddChildOrder(View view, int position, OrderItem<FavorStarOrder> data, String name) {
        mModel.addNewOrder(name, data.getBean().getId());
        mModel.loadOrders();
    }

    @Override
    protected void onEditOrder(View view, int position, OrderItem<FavorStarOrder> data, String name) {
        mModel.editOrder(data.getBean(), name);
        data.setName(name);
        orderAdapter.notifyItemChanged(position);
    }

    @Override
    protected void onDeleteOrder(View view, int position, OrderItem<FavorStarOrder> data) {
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
