package com.king.app.coolg.phone.order;

import android.animation.Animator;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentOrderPhoneBinding;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.SimpleDialogs;

import java.util.List;
import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 16:27
 */
public abstract class OrderFragment<VM extends BaseViewModel, T> extends MvvmFragment<FragmentOrderPhoneBinding, VM> {

    protected OrderAdapter<T> orderAdapter;

    protected IOrderHolder holder;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {
        this.holder = (IOrderHolder) holder;
    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_order_phone;
    }

    @Override
    protected void onCreate(View view) {
        mBinding.rvOrders.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mBinding.rvOrders.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                outRect.bottom = ScreenUtils.dp2px(8);
                if (position / 2 == 0) {
                    outRect.top = ScreenUtils.dp2px(8);
                }
            }
        });

        mBinding.indicator.addPath("Root");
        mBinding.indicator.setPathIndicatorListener((index, path) -> {
            if (mBinding.rvItems.getVisibility() == View.VISIBLE) {
                mBinding.rvItems.setVisibility(View.GONE);
                mBinding.rvOrders.setVisibility(View.VISIBLE);
            }
            backToDepth(index);
        });
        initItemsRecyclerView();
    }

    protected abstract void initItemsRecyclerView();

    protected abstract void backToDepth(int index);

    protected void showOrders(List<OrderItem<T>> list) {
        if (orderAdapter == null) {
            orderAdapter = new OrderAdapter<>();
            orderAdapter.setList(list);
            orderAdapter.setSetCoverMode(holder.isSetCoverMode());
            orderAdapter.setCheckMap(getCheckMap());
            orderAdapter.setOnEditListener(new OrderAdapter.OnEditListener<T>() {
                @Override
                public void onEdit(View view, int position, OrderItem<T> data) {
                    editOrder(view, position, data);
                }

                @Override
                public void onCheck(View v, int position, OrderItem<T> bean) {
                    checkOrder(v, position, bean);
                }
            });
            orderAdapter.setOnItemClickListener((view, position, data) -> onClickOrder(position, data));
            mBinding.rvOrders.setAdapter(orderAdapter);
        }
        else {
            orderAdapter.setList(list);
            orderAdapter.notifyDataSetChanged();
        }
    }

    protected abstract Map<Long,Boolean> getCheckMap();

    private void onClickOrder(int position, OrderItem<T> data) {
        if (data.isHasChild()) {
            mBinding.indicator.addPath(data.getName());
            loadFromOrder(data);
        }
        else {
            if (holder.isSetCoverMode()) {
                return;
            }
            else if (holder.isSelectMode()) {
                holder.onSelectOrder(data.getId());
            }
            else {
                mBinding.rvItems.setVisibility(View.VISIBLE);
                startRevealView(1000);
                mBinding.rvOrders.setVisibility(View.GONE);
                mBinding.indicator.addPath(data.getName());
                loadOrderItems(data);
            }
        }
    }

    private void startRevealView(int animTime) {
        Animator anim = ViewAnimationUtils.createCircularReveal(mBinding.rvItems, (int) mBinding.rvItems.getX()
                , (int) mBinding.rvItems.getY()
                , 0, (float) Math.hypot(mBinding.rvItems.getWidth(), mBinding.rvItems.getHeight()));
        anim.setDuration(animTime);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }

    private void checkOrder(View v, int position, OrderItem<T> data) {
        data.setImagePath(holder.getCoverPath());
        orderAdapter.notifyItemChanged(position);
        updateOrderCover(data, holder.getCoverPath());
    }

    protected abstract void updateOrderCover(OrderItem<T> data, String coverPath);

    protected abstract void loadOrderItems(OrderItem<T> data);

    protected abstract void loadFromOrder(OrderItem<T> data);

    private void editOrder(View view, int position, OrderItem<T> data) {
        PopupMenu menu = new PopupMenu(getActivity(), view);
        menu.getMenuInflater().inflate(R.menu.order_context, menu.getMenu());
        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_add:
                    new SimpleDialogs().openInputDialog(getActivity(), "Create new folder under " + data.getName(), name -> {
                        onAddChildOrder(view, position, data, name);
                    });
                    break;
                case R.id.menu_edit:
                    new SimpleDialogs().openInputDialog(getActivity(), "Edit " + data.getName(), name -> {
                        onEditOrder(view, position, data, name);
                    });
                    break;
                case R.id.menu_delete:
                    showConfirmMessage("Delete order will delete all related orders and items, continue?"
                            , (dialog, which) -> {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    onDeleteOrder(view, position, data);
                                }
                            });
                    break;
            }
            return true;
        });
        menu.show();
    }

    protected abstract void onAddOrder(String name);

    protected abstract void onAddChildOrder(View view, int position, OrderItem<T> data, String name);

    protected abstract void onEditOrder(View view, int position, OrderItem<T> data, String name);

    protected abstract void onDeleteOrder(View view, int position, OrderItem<T> data);

    public void addOrder() {
        new SimpleDialogs().openInputDialog(getActivity(), "Create new folder", name -> {
            onAddOrder(name);
        });
    }

    public boolean onBackPressed() {
        if (!mBinding.indicator.isRoot()) {
            mBinding.indicator.backToUpper();
            if (mBinding.rvItems.getVisibility() == View.VISIBLE) {
                mBinding.rvItems.setVisibility(View.GONE);
                mBinding.rvOrders.setVisibility(View.VISIBLE);
            }
            backToParent();
            return true;
        }
        return false;
    }

    protected abstract void backToParent();

    public void setSelectionMode(boolean selectionMode) {
        if (mBinding.rvItems.getVisibility() == View.VISIBLE) {
            setItemSelectionMode(selectionMode);
        }
        else {
            if (orderAdapter != null) {
                orderAdapter.setSelectionMode(selectionMode);
                orderAdapter.notifyDataSetChanged();
            }
        }
    }

    protected abstract void setItemSelectionMode(boolean selectionMode);

    public void delete() {
        showConfirmMessage("Delete order will delete all related orders and items, continue?"
                , (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        deleteSelectedItems();
                        holder.cancelConfirmStatus();
                    }
                });
    }

    protected abstract void deleteSelectedItems();

    public abstract void onSortTypeChanged();
}
