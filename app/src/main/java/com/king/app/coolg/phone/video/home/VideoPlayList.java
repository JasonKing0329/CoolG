package com.king.app.coolg.phone.video.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;
import com.king.app.gdb.data.entity.PlayOrder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:29
 */
public class VideoPlayList extends BaseObservable {

    private String imageUrl;

    private String name;

    private PlayOrder playOrder;

    private boolean isChecked;

    private int visibility;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public PlayOrder getPlayOrder() {
        return playOrder;
    }

    public void setPlayOrder(PlayOrder playOrder) {
        this.playOrder = playOrder;
    }

    @Bindable
    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        notifyPropertyChanged(BR.checked);
    }

    @Bindable
    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
        notifyPropertyChanged(BR.visibility);
    }
}
