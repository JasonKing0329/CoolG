package com.king.app.coolg.model.bean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2019/3/18 9:21
 */
public class BannerParams {

    private boolean isRandom = true;

    private int type;

    private int duration = 5000;

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
