package com.king.app.coolg.base.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/6 16:16
 */
public abstract class HeaderFooterBindingAdapter<VH extends ViewDataBinding, VF extends ViewDataBinding, VI extends ViewDataBinding, T>
        extends HeaderFooterRecyclerAdapter<T> {

    @Override
    protected RecyclerView.ViewHolder onCreateHeader(ViewGroup parent) {
        VH binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , getHeaderRes(), parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        return holder;
    }

    protected abstract int getHeaderRes();

    @Override
    protected RecyclerView.ViewHolder onCreateFooter(ViewGroup parent) {
        VF binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , getFooterRes(), parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        return holder;
    }

    protected abstract int getFooterRes();

    @Override
    protected RecyclerView.ViewHolder onCreateItem(ViewGroup parent) {
        VI binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                , getItemRes(), parent, false);
        BindingHolder holder = new BindingHolder(binding.getRoot());
        return holder;
    }

    protected abstract int getItemRes();

    @Override
    protected void onBindHeaderView(RecyclerView.ViewHolder holder) {
        VH binding = DataBindingUtil.getBinding(holder.itemView);
        onBindHead(binding);
        binding.executePendingBindings();
    }

    protected abstract void onBindHead(VH binding);

    @Override
    protected void onBindFooterView(RecyclerView.ViewHolder holder) {
        VF binding = DataBindingUtil.getBinding(holder.itemView);
        onBindFooter(binding);
        binding.executePendingBindings();
    }

    protected abstract void onBindFooter(VF binding);

    @Override
    protected void onBindItemView(RecyclerView.ViewHolder holder, int position) {
        VI binding = DataBindingUtil.getBinding(holder.itemView);
        onBindItem(binding, position, list.get(position));
        binding.executePendingBindings();
    }

    protected abstract void onBindItem(VI binding, int position, T object);

    public static class BindingHolder extends RecyclerView.ViewHolder {

        public BindingHolder(View itemView) {
            super(itemView);
        }
    }
}
