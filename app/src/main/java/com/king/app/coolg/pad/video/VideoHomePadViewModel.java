package com.king.app.coolg.pad.video;

import android.app.Application;
import android.support.annotation.NonNull;

import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.coolg.phone.video.home.VideoHomeViewModel;
import com.king.app.coolg.phone.video.home.VideoPlayList;

public class VideoHomePadViewModel extends VideoHomeViewModel {

    public VideoHomePadViewModel(@NonNull Application application) {
        super(application);
    }

    public void buildPage() {
        loadHeadData();
        loadRecommend();
    }

    public VideoGuy getGuy(int position) {
        try {
            return headDataObserver.getValue().getGuy(position);
        } catch (Exception e) {}
        return null;
    }

    public VideoPlayList getPlayList(int position) {
        try {
            return headDataObserver.getValue().getPlayList(position);
        } catch (Exception e) {}
        return null;
    }
}
