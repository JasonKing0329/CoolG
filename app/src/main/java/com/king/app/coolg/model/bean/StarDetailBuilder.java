package com.king.app.coolg.model.bean;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/7/31 17:07
 */
public class StarDetailBuilder {

    private boolean isLoadImagePath;

    private boolean isLoadImageSize;

    private boolean isLoadRating;

    /**
     * isLoadImageSize为true时，参照的缩放宽度
     */
    private int sizeBaseWidth;

    public boolean isLoadImagePath() {
        return isLoadImagePath;
    }

    public StarDetailBuilder setLoadImagePath(boolean loadImagePath) {
        isLoadImagePath = loadImagePath;
        return this;
    }

    public boolean isLoadImageSize() {
        return isLoadImageSize;
    }

    public int getSizeBaseWidth() {
        return sizeBaseWidth;
    }

    public StarDetailBuilder setLoadImageSize(boolean loadImageSize, int sizeBaseWidth) {
        isLoadImageSize = loadImageSize;
        this.sizeBaseWidth = sizeBaseWidth;
        return this;
    }

    public boolean isLoadRating() {
        return isLoadRating;
    }

    public StarDetailBuilder setLoadRating(boolean loadRating) {
        isLoadRating = loadRating;
        return this;
    }
}
