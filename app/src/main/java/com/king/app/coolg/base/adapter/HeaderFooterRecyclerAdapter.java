package com.king.app.coolg.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Administrator on 2017/5/20 0020.
 * 封装带header和footer的adapter
 * 处理position
 */

public abstract class HeaderFooterRecyclerAdapter<T> extends RecyclerView.Adapter {

    private final int TYPE_HEAD = 0;
    private final int TYPE_ITEM = 1;
    private final int TYPE_MORE = 2;

    protected List<T> list;

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_ITEM;
        if (position == 0) {
            type = TYPE_HEAD;
        }
        if (position == getItemCount() - 1) {
            type = TYPE_MORE;
        }
        return type;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            return onCreateItem(parent);
        }
        else if (viewType == TYPE_HEAD) {
            return onCreateHeader(parent);
        }
        else {
            return onCreateFooter(parent);
        }
    }

    protected abstract RecyclerView.ViewHolder onCreateHeader(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder onCreateFooter(ViewGroup parent);

    protected abstract RecyclerView.ViewHolder onCreateItem(ViewGroup parent);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            onBindHeaderView(holder);
        }
        else if (position == getItemCount() - 1) {
            onBindFooterView(holder);
        }
        else {
            // position 按照正常的以0开始
            onBindItemView(holder, position - 1);
        }
    }

    protected abstract void onBindHeaderView(RecyclerView.ViewHolder holder);

    protected abstract void onBindFooterView(RecyclerView.ViewHolder holder);

    protected abstract void onBindItemView(RecyclerView.ViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return list == null ? 2:list.size() + 2;// 首尾分别为header和footer
    }

}
