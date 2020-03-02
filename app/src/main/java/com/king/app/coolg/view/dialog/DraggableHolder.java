package com.king.app.coolg.view.dialog;

import android.view.View;

import com.king.app.coolg.base.IFragmentHolder;

/**
 * @desc
 * @auth 景阳
 * @time 2018/3/24 0024 23:18
 */

public interface DraggableHolder extends IFragmentHolder {
    void dismiss();
    void dismissAllowingStateLoss();
    View inflateToolbar(int layout);
}
