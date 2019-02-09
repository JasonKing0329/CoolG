package com.king.app.coolg.phone.star.list;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;
import com.king.app.coolg.phone.star.category.SelectObserver;
import com.king.app.gdb.data.entity.Star;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:01
 */
public class StarProxy extends BaseObservable {
    private Star star;
    private String imagePath;

    private boolean isChecked;

    private SelectObserver<StarProxy> observer;

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Bindable
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        notifyPropertyChanged(BR.checked);
    }

    public SelectObserver<StarProxy> getObserver() {
        return observer;
    }

    public void setObserver(SelectObserver<StarProxy> observer) {
        this.observer = observer;
    }
}
