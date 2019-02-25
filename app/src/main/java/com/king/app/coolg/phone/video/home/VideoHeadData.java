package com.king.app.coolg.phone.video.home;

import android.view.View;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/2/22 16:37
 */
public class VideoHeadData {

    private List<VideoGuy> guyList;

    private List<VideoPlayList> playLists;

    public void setGuyList(List<VideoGuy> guyList) {
        this.guyList = guyList;
    }

    public void setPlayLists(List<VideoPlayList> playLists) {
        this.playLists = playLists;
    }

    public int getPlayListVisibility(int position) {
        if (playLists != null && position < playLists.size()) {
            return View.VISIBLE;
        }
        else {
            return View.GONE;
        }
    }

    public String getPlayListUrl(int position) {
        if (playLists != null && position < playLists.size()) {
            return playLists.get(position).getImageUrl();
        }
        else {
            return null;
        }
    }

    public String getPlayListName(int position) {
        if (playLists != null && position < playLists.size()) {
            return playLists.get(position).getName();
        }
        else {
            return null;
        }
    }

    public VideoPlayList getPlayList(int position) {
        if (playLists != null && position < playLists.size()) {
            return playLists.get(position);
        }
        else {
            return null;
        }
    }

    public int getGuyVisibility(int position) {
        if (guyList != null && position < guyList.size()) {
            return View.VISIBLE;
        }
        else {
            return View.GONE;
        }
    }

    public String getGuyUrl(int position) {
        if (guyList != null && position < guyList.size()) {
            return guyList.get(position).getImageUrl();
        }
        else {
            return null;
        }
    }

    public String getGuyName(int position) {
        if (guyList != null && position < guyList.size()) {
            return guyList.get(position).getStar().getName();
        }
        else {
            return null;
        }
    }

    public VideoGuy getGuy(int position) {
        if (guyList != null && position < guyList.size()) {
            return guyList.get(position);
        }
        else {
            return null;
        }
    }

}
