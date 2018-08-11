package com.king.app.coolg.phone.record.list;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogSortRecordBinding;
import com.king.app.coolg.model.setting.PreferenceValue;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Created by Administrator on 2018/8/11 0011.
 */

public class SortDialogContent extends DraggableContentFragment<FragmentDialogSortRecordBinding> {

    private ItemAdapter itemAdapter;

    private boolean mDesc;

    private int mSortType;

    private OnSortListener onSortListener;

    private SortItem[] items = new SortItem[] {
            new SortItem("None", PreferenceValue.GDB_SR_ORDERBY_NONE)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_NAME, PreferenceValue.GDB_SR_ORDERBY_NAME)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_DATE, PreferenceValue.GDB_SR_ORDERBY_DATE)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_SCORE, PreferenceValue.GDB_SR_ORDERBY_SCORE)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_PASSION, PreferenceValue.GDB_SR_ORDERBY_PASSION)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_CUM, PreferenceValue.GDB_SR_ORDERBY_CUM)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_FEEL, PreferenceValue.GDB_SR_ORDERBY_SCOREFEEL)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_SPECIAL, PreferenceValue.GDB_SR_ORDERBY_SPECIAL)
            , new SortItem(PreferenceValue.SORT_COLUMN_KEY_STAR, PreferenceValue.GDB_SR_ORDERBY_STAR)
    };

    private int textPadding;
    private int focusColor;

    @Override
    protected void initView() {
        textPadding = ScreenUtils.dp2px(20);
        focusColor = getResources().getColor(R.color.actionbar_bk_orange);

        itemAdapter = new ItemAdapter();
        // 初始化升序/降序
        if (!mDesc) {
            mBinding.rbAsc.setChecked(true);
        }
        // 初始化当前排序类型
        for (int i = 0; i < items.length; i ++) {
            if (mSortType == items[i].value) {
                itemAdapter.setSelection(i);
                break;
            }
        }

        mBinding.gridView.setAdapter(itemAdapter);
        mBinding.gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            itemAdapter.setSelection(position);
            itemAdapter.notifyDataSetChanged();
        });

        mBinding.tvOk.setOnClickListener(view -> onSave());
    }

    public void onSave() {

        if (onSortListener != null) {
            onSortListener.onSort(mBinding.rbDesc.isChecked(), items[itemAdapter.getSelectedIndex()].value);
        }
        dismissAllowingStateLoss();
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_sort_record;
    }

    public void setDesc(boolean mDesc) {
        this.mDesc = mDesc;
    }

    public void setSortType(int mSortType) {
        this.mSortType = mSortType;
    }

    public void setOnSortListener(OnSortListener onSortListener) {
        this.onSortListener = onSortListener;
    }

    private class SortItem {
        String name;
        int value;
        public SortItem(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }

    private class ItemAdapter extends BaseAdapter {

        private int selectedIndex;

        public void setSelection(int position) {
            selectedIndex = position;
        }

        public int getSelectedIndex() {
            return selectedIndex;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(getContext());
            textView.setText(items[position].name);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(0, textPadding, 0, textPadding);
            if (position == selectedIndex) {
                textView.setBackgroundColor(focusColor);
            }
            else {
                textView.setBackground(null);
            }
            return textView;
        }
    }

    public interface OnSortListener {
        void onSort(boolean desc, int sortMode);
    }
}
