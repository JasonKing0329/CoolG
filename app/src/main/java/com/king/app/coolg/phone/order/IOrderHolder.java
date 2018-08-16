package com.king.app.coolg.phone.order;

import com.king.app.coolg.base.IFragmentHolder;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/14 17:00
 */
public interface IOrderHolder extends IFragmentHolder {

    void cancelConfirmStatus();

    boolean isSetCoverMode();
    String getCoverPath();

    boolean isSelectMode();

    void onSelectOrder(long id);
}
