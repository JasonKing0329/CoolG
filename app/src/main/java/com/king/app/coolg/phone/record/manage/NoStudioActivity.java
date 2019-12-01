package com.king.app.coolg.phone.record.manage;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.widget.LinearLayoutManager;

import com.chenenyu.router.Router;
import com.chenenyu.router.annotation.Route;
import com.king.app.coolg.R;
import com.king.app.coolg.base.MvvmActivity;
import com.king.app.coolg.databinding.ActivityRecordNoStudioBinding;
import com.king.app.coolg.phone.record.RecordActivity;
import com.king.app.coolg.phone.record.list.RecordListAdapter;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/11/18 14:15
 */
@Route("RecordNoStudio")
public class NoStudioActivity extends MvvmActivity<ActivityRecordNoStudioBinding, NoStudioViewModel> {

    private RecordListAdapter listAdapter;

    @Override
    protected int getContentView() {
        return R.layout.activity_record_no_studio;
    }

    @Override
    protected void initView() {
        mBinding.actionbar.setOnBackListener(() -> onBackPressed());

        mBinding.rvList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected NoStudioViewModel createViewModel() {
        return ViewModelProviders.of(this).get(NoStudioViewModel.class);
    }

    @Override
    protected void initData() {
        mModel.listObserver.observe(this, list -> showList(list));

        mModel.loadData();
    }

    private void showList(List<RecordProxy> list) {
        if (listAdapter == null) {
            listAdapter = new RecordListAdapter();
            listAdapter.setList(list);
            listAdapter.setOnItemClickListener((view, position, data) -> goToRecordPage(data.getRecord()));
            mBinding.rvList.setAdapter(listAdapter);
        }
        else {
            listAdapter.setList(list);
            listAdapter.notifyDataSetChanged();
        }
    }

    private void goToRecordPage(Record data) {
        Router.build("RecordPhone")
                .with(RecordActivity.EXTRA_RECORD_ID, data.getId())
                .go(this);
    }

}
