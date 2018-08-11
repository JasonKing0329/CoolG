package com.king.app.coolg.phone.record.list;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogFilterRecordBinding;
import com.king.app.coolg.model.bean.RecordListFilterBean;
import com.king.app.coolg.view.dialog.DraggableContentFragment;

/**
 * Created by Administrator on 2018/8/11 0011.
 */

public class FilterDialogContent extends DraggableContentFragment<FragmentDialogFilterRecordBinding> {

    private RecordListFilterBean filterBean;

    private OnFilterListener onFilterListener;

    @Override
    protected void initView() {

        mBinding.tvTagBareback.setOnClickListener(v -> {
            mBinding.tvTagBareback.setSelected(!mBinding.tvTagBareback.isSelected());
            filterBean.setBareback(mBinding.tvTagBareback.isSelected());
        });
        mBinding.tvTagInner.setOnClickListener(v -> {
            mBinding.tvTagInner.setSelected(!mBinding.tvTagInner.isSelected());
            filterBean.setInnerCum(mBinding.tvTagInner.isSelected());
        });
        mBinding.tvTagNd.setOnClickListener(v -> {
            mBinding.tvTagNd.setSelected(!mBinding.tvTagNd.isSelected());
            filterBean.setNotDeprecated(mBinding.tvTagNd.isSelected());
        });

        mBinding.tvOk.setOnClickListener(v -> onSave());

        if (filterBean == null) {
            filterBean = new RecordListFilterBean();
        }
        else {
            if (filterBean.isBareback()) {
                mBinding.tvTagBareback.setSelected(true);
            }
            if (filterBean.isInnerCum()) {
                mBinding.tvTagInner.setSelected(true);
            }
            if (filterBean.isNotDeprecated()) {
                mBinding.tvTagNd.setSelected(true);
            }
        }
    }

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_filter_record;
    }

    public void setFilterBean(RecordListFilterBean filterBean) {
        this.filterBean = filterBean;
    }

    public void setOnFilterListener(OnFilterListener onFilterListener) {
        this.onFilterListener = onFilterListener;
    }

    public void onSave() {
        if (onFilterListener != null) {
            onFilterListener.onFilter(filterBean);
        }
        dismissAllowingStateLoss();
    }

    public interface OnFilterListener {
        void onFilter(RecordListFilterBean bean);
    }
}
