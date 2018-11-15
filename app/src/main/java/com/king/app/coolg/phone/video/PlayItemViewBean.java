package com.king.app.coolg.phone.video;

import com.king.app.gdb.data.entity.PlayItem;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 14:36
 */
public class PlayItemViewBean {

    private PlayItem playItem;

    private String cover;

    public PlayItem getPlayItem() {
        return playItem;
    }

    public void setPlayItem(PlayItem playItem) {
        this.playItem = playItem;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
