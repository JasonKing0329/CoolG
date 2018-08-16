package com.king.app.coolg.phone.order.star;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import com.chenenyu.router.Router;
import com.king.app.coolg.phone.order.OrderFragment;
import com.king.app.coolg.phone.order.OrderItem;
import com.king.app.coolg.phone.star.StarActivity;
import com.king.app.coolg.phone.star.list.StarCircleAdapter;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.gdb.data.entity.FavorStarOrder;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 13:24
 */
public class StarOrderFragment extends OrderFragment<StarOrderViewModel, FavorStarOrder> {

    private StarCircleAdapter starAdapter;

    @Override
    protected void initItemsRecyclerView() {
        mBinding.rvItems.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mModel.starItemsObserver.observe(this, list -> {
            if (starAdapter == null) {
                starAdapter = new StarCircleAdapter();
                starAdapter.setList(list);
                starAdapter.setCheckMap(mModel.getItemCheckMap());
                starAdapter.setOnItemClickListener((view, position, data) -> goToStarPage(data));
                mBinding.rvItems.setAdapter(starAdapter);
            }
            else {
                starAdapter.setList(list);
                starAdapter.notifyDataSetChanged();
            }
        });
    }

    private void goToStarPage(StarProxy data) {
        Router.build("StarPhone")
                .with(StarActivity.EXTRA_STAR_ID, data.getStar().getId())
                .go(this);
    }

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
        return mModel.getOrderCheckMap();
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
    protected void setItemSelectionMode(boolean selectionMode) {
        if (starAdapter != null) {
            starAdapter.setSelectionMode(selectionMode);
            starAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void deleteSelectedItems() {
        if (mBinding.rvItems.getVisibility() == View.VISIBLE) {
            mModel.deleteSelectedItems(true);
            mModel.loadStarItems();
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
    protected void updateOrderCover(OrderItem<FavorStarOrder> data, String coverPath) {
        mModel.updateCover(data.getBean(), coverPath);
    }

    @Override
    protected void loadOrderItems(OrderItem<FavorStarOrder> data) {
        mModel.loadStarItems(data.getBean());
    }
}
