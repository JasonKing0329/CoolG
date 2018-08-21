package com.king.app.coolg.phone.star.list;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.gdb.data.entity.Star;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/12 10:39
 */
public interface IStarListHolder extends IFragmentHolder {

    boolean dispatchClickStar(Star star);

    void hideDetailIndex();

    void updateDetailIndex(String name);

    boolean dispatchOnLongClickStar(Star star);
}
