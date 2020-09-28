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

    public void updatePlayMode(int mode) {
        PlayList playList = getPlayList();
        playList.setPlayMode(mode);
        saveList(playList);
    }

    public void setPlayIndexAsLast() {
        PlayList playList = getPlayList();
        if (playList.getList().size() > 0) {
            playList.setPlayIndex(playList.getList().size() - 1);
        }
        else {
            playList.setPlayIndex(0);
        }
        saveList(playList);
    }

    private int findExistedItem(PlayList playList, String url, long recordId) {
        int existIndex = -1;
        for (int i = 0; i < playList.getList().size(); i ++) {
            PlayList.PlayItem item = playList.getList().get(i);
            // 先判断recordId
            if (recordId > 0 && item.getRecordId() == recordId) {
                existIndex = i;
                break;
            }
            // 再判断url
            if (item.getUrl() != null && item.getUrl().equals(url)) {
                existIndex = i;
                break;
            }
        }
        return existIndex;
    }

    public void addUrl(String url) {
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, url, 0);
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(url);
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
            playList.getList().remove(existIndex);
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        saveList(playList);
    }

    public void addPlayItemViewBean(PlayItemViewBean bean) {
        if (bean.getRecord() == null) {
            return;
        }
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, bean.getPlayUrl(), bean.getRecord().getId());
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(bean.getPlayUrl());
        item.setRecordId(bean.getRecord().getId());
        item.setName(bean.getRecord().getName());
//        item.setDuration();
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
            playList.getList().remove(existIndex);
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        saveList(playList);
    }

    public void addRecord(Record record, String url) {
        if (record == null) {
            return;
        }
        PlayList playList = getPlayList();
        int existIndex = findExistedItem(playList, url, record.getId());
        PlayList.PlayItem item = new PlayList.PlayItem();
        item.setUrl(url);
        item.setRecordId(record.getId());
        item.setName(record.getName());
        // 已有则删除并重新加在末尾，但保留播放时长
        if (existIndex != -1) {
            item.setPlayTime(playList.getList().get(existIndex).getPlayTime());
            playList.getList().remove(existIndex);
        }
        item.setIndex(playList.getList().size());
        playList.getList().add(item);
        saveList(playList);
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
            if (item.getRecordId() == playItem.getRecordId()) {
                playList.getList().remove(i);
                break;
            }
            if (playItem.getUrl() != null && playItem.getUrl().equals(item.getUrl())) {
                playList.getList().remove(i);
                break;
            }
        }
        saveList(playList);
    }

    private void saveList(PlayList playList) {
        SettingProperty.setPlayList(playList);
    }

    public void clearPlayList() {
        PlayList playList = getPlayList();
        playList.getList().clear();
        playList.setPlayIndex(0);
        saveList(playList);
    }

    public void updatePlayItem(PlayList.PlayItem item) {
        PlayList playList = getPlayList();
        int index = findExistedItem(playList, item.getUrl(), item.getRecordId());
        try {
            playList.getList().set(index, item);
            saveList(playList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
