package com.king.app.coolg.phone.studio.page;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.ItemDataBinder;
import com.king.app.coolg.databinding.AdapterRecordItemListBinding;
import com.king.app.coolg.phone.record.list.RecordItemBinder;
import com.king.app.coolg.phone.record.list.RecordProxy;

import java.util.Map;

/**
 * Desc: 混合布局里的record列表布局，关联实体RecordProxy
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:14
 */
public class RecordAdapter extends ItemDataBinder<RecordProxy, AdapterRecordItemListBinding> {

    private int mSortMode;

    private RecordItemBinder binder;
    private boolean selectionMode;
    private Map<Long, Boolean> mCheckMap;

    private OnClickRecordListener onClickRecordListener;

    public RecordAdapter() {
        binder = new RecordItemBinder();
    }

    public void setOnClickRecordListener(OnClickRecordListener onClickRecordListener) {
        this.onClickRecordListener = onClickRecordListener;
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

    @Override
    protected void bindModel(RecordProxy item, AdapterRecordItemListBinding binding) {
        binder.setSortMode(mSortMode);
        binder.setSelectionMode(selectionMode);
        binder.setCheckMap(mCheckMap);
        binder.bind(binding, item.getOffsetIndex(), item);
        binding.getRoot().setOnClickListener(v -> {
            if (onClickRecordListener != null) {
                onClickRecordListener.onClickRecord(item);
            }
        });
    }

    @Override
    protected AdapterRecordItemListBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return AdapterRecordItemListBinding.inflate(inflater);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof RecordProxy;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }

    public interface OnClickRecordListener {
        void onClickRecord(RecordProxy recordProxy);
    }
}
