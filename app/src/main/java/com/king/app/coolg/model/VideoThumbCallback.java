package com.king.app.coolg.model;

import android.graphics.Bitmap;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/19 16:15
 */
public interface VideoThumbCallback {
    void onThumbnailCreated(List<Bitmap> list);
}
