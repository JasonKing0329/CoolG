package com.king.app.coolg.phone.video.home;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.app.coolg.BR;
import com.king.app.gdb.data.entity.Star;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:29
 */
public class VideoGuy extends BaseObservable {

    private Star star;

    private String imageUrl;

    private int videos;

    private boolean isChecked;

    private int visibility;

    public Star getStar() {
        return star;
    }

    public void setStar(Star star) {
        this.star = star;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getVideos() {
        return videos;
    }

    public void setVideos(int videos) {
        this.videos = videos;
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
