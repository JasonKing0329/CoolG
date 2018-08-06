package com.king.app.coolg.utils;

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

/**
 * 描述: string signature
 * <p/>作者：景阳
 * <p/>创建时间: 2017/8/23 17:31
 */
public class GlideStringKey implements Key {

    private String strKey;

    public GlideStringKey(String key) {
        this.strKey = key;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GlideStringKey) {
            GlideStringKey key = (GlideStringKey) obj;
            return strKey.equals(key.getStrKey());
        }
        else {
            return false;
        }
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(strKey.toString().getBytes(CHARSET));
    }

    public String getStrKey() {
        return strKey;
    }
}
