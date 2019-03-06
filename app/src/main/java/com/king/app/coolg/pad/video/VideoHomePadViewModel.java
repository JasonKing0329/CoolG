package com.king.app.coolg.pad.video;

import android.app.Application;
import android.support.annotation.NonNull;

import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.video.home.VideoGuy;
import com.king.app.coolg.phone.video.home.VideoHeadData;
import com.king.app.coolg.phone.video.home.VideoHomeViewModel;
import com.king.app.coolg.phone.video.home.VideoPlayList;
import com.king.app.gdb.data.entity.PlayOrder;
import com.king.app.gdb.data.entity.VideoCoverStar;

import java.util.List;

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

    @Override
    protected void getPadCovers(VideoHeadData data) {
        List<PlayOrder> orders = getDaoSession().getPlayOrderDao().queryBuilder()
                .orderRaw("RANDOM()")
                .limit(1)
                .build().list();
        if (orders.size() > 0) {
            data.setPadPlayListCover(ImageProvider.parseCoverUrl(orders.get(0).getCoverUrl()));
        }
        List<VideoCoverStar> stars = getDaoSession().getVideoCoverStarDao().queryBuilder()
                .orderRaw("RANDOM()")
                .limit(1)
                .build().list();
        if (stars.size() > 0) {
            data.setPadGuyCover(ImageProvider.getStarRandomPath(stars.get(0).getStar().getName(), null));
        }
    }
}
