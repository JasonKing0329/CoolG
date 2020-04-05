package com.king.app.coolg.phone.record.list;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemGridBinding;

import java.util.Map;

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/5 17:36
 */
public class RecordGridAdapter extends BaseBindingAdapter<AdapterRecordItemGridBinding, RecordProxy> {

    private int mSortMode;
    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;

    private RecordItemGridBinder binder;
    private RecordItemGridBinder.OnPopupListener popupListener;

    public RecordGridAdapter() {
        binder = new RecordItemGridBinder();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }

    public void setPopupListener(RecordItemGridBinder.OnPopupListener popupListener) {
        this.popupListener = popupListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_item_grid;
    }

    @Override
    protected void onBindItem(AdapterRecordItemGridBinding binding, int position, RecordProxy bean) {
        binder.setSortMode(mSortMode);
        binder.setSelectionMode(selectionMode);
        binder.setCheckMap(mCheckMap);
        binder.setPopupListener(popupListener);
        binder.bind(binding, position, bean);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (selectionMode) {
            long key = list.get(position).getRecord().getId();
            if (mCheckMap.get(key) == null) {
                mCheckMap.put(key, true);
            }
            else {
                mCheckMap.remove(key);
            }
            notifyItemChanged(position);
        }
        else {
            super.onClickItem(v, position);
        }
    }

}
