package com.king.app.coolg.phone.image;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/4 9:53
 */
public class ImageBean extends BaseObservable {

    private String url;

    private int width;

    private int height;

    private boolean isSelected;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Bindable
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
        notifyPropertyChanged(BR.selected);
    }
}
