package com.king.app.coolg.phone.record.list;

import android.app.Application;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.repository.RecordRepository;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/10 16:44
 */
public class RecordListViewModel extends BaseViewModel {

    private RecordRepository repository;

    public RecordListViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
    }

    public void loadRecords(String type) {

    }
}
