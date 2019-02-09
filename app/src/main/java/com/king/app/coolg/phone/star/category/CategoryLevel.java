package com.king.app.coolg.phone.star.category;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;

import java.util.List;

public class CategoryLevel extends BaseObservable {

    private int level;

    private boolean isSelected;

    private List<Object> starList;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    public List<Object> getStarList() {
        return starList;
    }

    public void setStarList(List<Object> starList) {
        this.starList = starList;
    }
}
