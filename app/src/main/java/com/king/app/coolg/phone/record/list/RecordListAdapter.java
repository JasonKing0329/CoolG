package com.king.app.coolg.phone.record.list;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:01
 */
public class RecordListAdapter extends BaseBindingAdapter<AdapterRecordItemListBinding, RecordProxy> {

    private int mSortMode;

    private RecordItemBinder binder;

    public RecordListAdapter() {
        binder = new RecordItemBinder();
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_record_item_list;
    }

    @Override
    protected void onBindItem(AdapterRecordItemListBinding binding, int position, RecordProxy item) {
        binder.setSortMode(mSortMode);
        binder.bind(binding, position, item);
    }

    public void setSortMode(int mSortMode) {
        this.mSortMode = mSortMode;
    }
}
