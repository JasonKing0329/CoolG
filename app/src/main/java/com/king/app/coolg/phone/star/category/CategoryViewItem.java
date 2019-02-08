package com.king.app.coolg.phone.star.category;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;
import com.king.app.gdb.data.entity.TopStarCategory;

public class CategoryViewItem extends BaseObservable {

    private TopStarCategory category;

    private boolean isSelected;

    private int visibility;

    private SelectObserver<CategoryViewItem> observer;

    public TopStarCategory getCategory() {
        return category;
    }

    public void setCategory(TopStarCategory category) {
        this.category = category;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }

    @Bindable
    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }

    public SelectObserver<CategoryViewItem> getObserver() {
        return observer;
    }

    public void setObserver(SelectObserver<CategoryViewItem> observer) {
        this.observer = observer;
    }
}
