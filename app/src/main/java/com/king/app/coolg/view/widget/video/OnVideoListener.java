package com.king.app.coolg.view.widget.video;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/11/15 15:29
 */
public interface OnVideoListener {
    int getStartSeek();
    void updatePlayPosition(int currentPosition);
    void onPlayComplete();
    void onPause();
    void onDestroy();
    void onStart();
}
