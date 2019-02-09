package com.king.app.coolg.base.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public abstract class TwoTypeBindingAdapter<V1 extends ViewDataBinding, V2 extends ViewDataBinding, T1, T2> extends RecyclerView.Adapter {

    private final int TYPE1 = 1;
    private final int TYPE2 = 2;

    protected List<Object> list;

    protected OnItemClickListener<T1, T2> onItemClickListener;

    protected OnItemLongClickListener<T1, T2> onItemLongClickListener;

    public void setList(List<Object> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener<T1, T2> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T1, T2> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getClass() == getType1Class()) {
            return TYPE1;
        }
        else if (list.get(position).getClass() == getType2Class()) {
            return TYPE2;
        }
        return 0;
    }

    protected abstract Class getType1Class();

    protected abstract Class getType2Class();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE1) {
            V1 binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , getType1LayoutRes(), parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getLayoutPosition();
                onClickType1(v, position);
            });
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getLayoutPosition();
                onLongClickType1(v, position);
                return true;
            });
            return holder;
        }
        else if (viewType == TYPE2) {
            V2 binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext())
                    , getType2LayoutRes(), parent, false);
            BindingHolder holder = new BindingHolder(binding.getRoot());
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getLayoutPosition();
                onClickType2(v, position);
            });
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getLayoutPosition();
                onLongClickType2(v, position);
                return true;
            });
            return holder;
        }
        return null;
    }

    protected abstract int getType1LayoutRes();

    protected abstract int getType2LayoutRes();

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE1) {
            V1 binding = DataBindingUtil.getBinding(holder.itemView);
            onBindType1(binding, position, (T1) list.get(position));
            binding.executePendingBindings();
        }
        else if (getItemViewType(position) == TYPE2) {
            V2 binding = DataBindingUtil.getBinding(holder.itemView);
            onBindType2(binding, position, (T2) list.get(position));
            binding.executePendingBindings();
        }
    }

    protected abstract void onBindType1(V1 binding, int position, T1 bean);

    protected abstract void onBindType2(V2 binding, int position, T2 bean);

    protected void onClickType1(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickType1(v, position, (T1) list.get(position));
        }
    }

    protected void onClickType2(View v, int position) {
        if (onItemClickListener != null) {
            onItemClickListener.onClickType2(v, position, (T2) list.get(position));
        }
    }

    protected void onLongClickType1(View v, int position) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onLongClickType1(v, position, (T1) list.get(position));
        }
    }

    protected void onLongClickType2(View v, int position) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onLongClickType2(v, position, (T2) list.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    public static class BindingHolder extends RecyclerView.ViewHolder {

        public BindingHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnItemClickListener<T1, T2> {
        void onClickType1(View view, int position, T1 data);
        void onClickType2(View view, int position, T2 data);
    }

    public interface OnItemLongClickListener<T1, T2> {
        void onLongClickType1(View view, int position, T1 data);
        void onLongClickType2(View view, int position, T2 data);
    }

}
