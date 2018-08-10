package com.king.app.coolg.phone.record.list;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.base.MvvmFragment;
import com.king.app.coolg.databinding.FragmentRecordListBinding;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:44
 */
public class RecordListFragment extends MvvmFragment<FragmentRecordListBinding, RecordListViewModel> {

    private static final String ARG_RECORD_TYPE = "record_type";

    public static RecordListFragment newInstance(String type) {

        RecordListFragment fragment = new RecordListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_RECORD_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    private RecordListAdapter listAdapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_record_list;
    }

    @Override
    protected RecordListViewModel createViewModel() {
        return ViewModelProviders.of(this).get(RecordListViewModel.class);
    }

    @Override
    protected void onCreate(View view) {
        mBinding.rvRecords.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onCreateData() {

    }
}
