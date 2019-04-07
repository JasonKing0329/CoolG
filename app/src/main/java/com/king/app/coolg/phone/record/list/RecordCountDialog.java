package com.king.app.coolg.phone.record.list;

import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;

import com.king.app.coolg.R;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.coolg.databinding.FragmentDialogRecordCountBinding;
import com.king.app.coolg.model.bean.TitleValueBean;
import com.king.app.coolg.view.SimpleTitleValueAdapter;
import com.king.app.coolg.view.dialog.DraggableContentFragment;
import com.king.app.gdb.data.entity.RecordDao;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RecordCountDialog extends DraggableContentFragment<FragmentDialogRecordCountBinding> {

    private SimpleTitleValueAdapter adapter;

    @Override
    protected void bindFragmentHolder(IFragmentHolder holder) {

    }

    @Override
    protected int getContentLayoutRes() {
        return R.layout.fragment_dialog_record_count;
    }

    @Override
    protected void initView() {
        mBinding.rvItems.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        count();
    }

    private void count() {
        getData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<TitleValueBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<TitleValueBean> list) {
                        if (adapter == null) {
                            adapter = new SimpleTitleValueAdapter();
                            adapter.setList(list);
                            mBinding.rvItems.setAdapter(adapter);
                        }
                        else {
                            adapter.setList(list);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showMessageShort(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<TitleValueBean>> getData() {
        return Observable.create(e -> {
            List<TitleValueBean> list = new ArrayList<>();
            RecordDao dao = CoolApplication.getInstance().getDaoSession().getRecordDao();
            Cursor cursor = CoolApplication.getInstance().getDatabase().rawQuery("SELECT MAX(score) FROM " + RecordDao.TABLENAME, new String[]{});
            int max = 0;
            if (cursor.moveToNext()) {
                max = cursor.getInt(0);
            }
            max = max / 10 * 10;// 取整

            int score = max;
            while (score > 299) {
                long count = dao.queryBuilder()
                        .where(RecordDao.Properties.Score.ge(score))
                        .where(RecordDao.Properties.Score.lt(score + 10))
                        .buildCount().count();
                list.add(new TitleValueBean(score + "+", String.valueOf(count)));

                score -= 10;
            }
            long count = dao.queryBuilder()
                    .where(RecordDao.Properties.Score.gt(0))
                    .where(RecordDao.Properties.Score.lt(300))
                    .buildCount().count();
            list.add(new TitleValueBean("0-300", String.valueOf(count)));

            count = dao.queryBuilder()
                    .where(RecordDao.Properties.Score.eq(0))
                    .buildCount().count();
            list.add(new TitleValueBean("0", String.valueOf(count)));
            e.onNext(list);
        });
    }
}
