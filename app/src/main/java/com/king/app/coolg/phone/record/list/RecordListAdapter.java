package com.king.app.coolg.phone.record.list;

import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;

import java.util.Map;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:01
 */
public class RecordListAdapter extends BaseBindingAdapter<AdapterRecordItemListBinding, RecordProxy> {

    private int mSortMode;

    private RecordItemBinder binder;
    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;

    public RecordListAdapter() {
        binder = new RecordItemBinder();
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setCheckMap(Map<Long, Boolean> mCheckMap) {
        this.mCheckMap = mCheckMap;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_item_list;
    }

    @Override
    protected void onBindItem(AdapterRecordItemListBinding binding, int position, RecordProxy item) {
        binder.setSortMode(mSortMode);
        binder.setSelectionMode(selectionMode);
        binder.setCheckMap(mCheckMap);
        binder.bind(binding, position, item);
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
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
