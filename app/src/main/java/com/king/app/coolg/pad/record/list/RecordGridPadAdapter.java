package com.king.app.coolg.pad.record.list;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemGridPadBinding;
import com.king.app.coolg.pad.record.list.RecordItemBinder;
import com.king.app.coolg.phone.record.list.RecordProxy;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/17 15:09
 */
public class RecordGridPadAdapter extends BaseBindingAdapter<AdapterRecordItemGridPadBinding, RecordProxy> {

    private int mSortMode;
    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;

    private RecordItemBinder binder;
    private RecordItemBinder.OnPopupListener popupListener;

    public RecordGridPadAdapter() {
        binder = new RecordItemBinder();
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

    public void setPopupListener(RecordItemBinder.OnPopupListener popupListener) {
        this.popupListener = popupListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_item_grid_pad;
    }

    @Override
    protected void onBindItem(AdapterRecordItemGridPadBinding binding, int position, RecordProxy bean) {
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
