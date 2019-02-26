package com.king.app.coolg.phone.video.home;

import android.app.Application;
import android.databinding.ObservableInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;

import com.king.app.coolg.base.BaseViewModel;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/26 17:44
 */
public class RecommendViewModel extends BaseViewModel {

    public ObservableInt groupFTypeVisibility = new ObservableInt(View.GONE);

    public RecommendViewModel(@NonNull Application application) {
        super(application);
    }

    public CompoundButton.OnCheckedChangeListener fTypeCheckListener = (buttonView, isChecked) -> {
        groupFTypeVisibility.set(isChecked ? View.VISIBLE:View.GONE);
    };
}
