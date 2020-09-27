package com.king.app.coolg.phone.video.player;

import com.king.app.coolg.model.bean.PlayList;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.video.list.PlayItemViewBean;
import com.king.app.gdb.data.entity.Record;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/9/27 10:49
 */
public class PlayListInstance {

    private static PlayListInstance instance;

    private PlayList playList;

    public static PlayListInstance getInstance() {
        synchronized (PlayListInstance.class) {
            if (instance == null) {
                instance = new PlayListInstance();
            }
        }
        return instance;
    }

    public void destroy() {
        instance = null;
    }

    private PlayListInstance() {

    }

    public PlayList getPlayList() {
        return SettingProperty.getPlayList();
    }

    public void setPlayIndexAsLast() {
        PlayList playList = getPlayList();
        if (playList.getList().size() > 0) {
            playList.setPlayIndex(playList.getList().size() - 1);
        }
        else {
            playList.setPlayIndex(0);
        }
    }

    private int findExistedItem(PlayList playList, String url) {
        int existIndex = -1;
        for (int i = 0; i < playList.getList().size(); i ++) {
            PlayList.PlayItem item = playList.getList().get(i);
            if (item.getUrl().equals(url)) {
                existIndex = i;
                break;
            }
        }
        return existIndex;
    }

    public void addUrl(String url) {
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, url);
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(url);
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            playList.getList().remove(existIndex);
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        SettingProperty.setPlayList(playList);
    }

    public void addPlayItemViewBean(PlayItemViewBean bean) {
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, bean.getPlayUrl());
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(bean.getPlayUrl());
        item.setRecordId(bean.getRecord().getId());
        item.setName(bean.getRecord().getName());
//        item.setDuration();
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            playList.getList().remove(existIndex);
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        SettingProperty.setPlayList(playList);
    }

    public void addRecord(Record record, String url) {
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, url);
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(url);
        item.setRecordId(record.getId());
        item.setName(record.getName());
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            playList.getList().remove(existIndex);
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        SettingProperty.setPlayList(playList);
    }

    public void addPlayItems(List<PlayItemViewBean> list) {
        for (PlayItemViewBean bean:list) {
            addPlayItemViewBean(bean);
        }
    }

    public void deleteItem(PlayList.PlayItem item) {
        PlayList playList = getPlayList();
        for (int i = 0; i < playList.getList().size(); i ++) {
            PlayList.PlayItem playItem = playList.getList().get(i);
            if (playItem.getUrl().equals(item.getUrl())) {
                playList.getList().remove(i);
                break;
            }
        }
        SettingProperty.setPlayList(playList);
    }

    public void updatePlayList() {
        for (int i = 0; i < playList.getList().size(); i ++) {
            PlayList.PlayItem playItem = playList.getList().get(i);
            playItem.setIndex(i);
        }
        SettingProperty.setPlayList(playList);
    }
}
