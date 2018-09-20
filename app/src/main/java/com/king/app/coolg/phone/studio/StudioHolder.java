package com.king.app.coolg.phone.studio;

import com.king.app.coolg.base.IFragmentHolder;
import com.king.app.jactionbar.JActionbar;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/9/18 13:39
 */
public interface StudioHolder extends IFragmentHolder {

    JActionbar getJActionBar();

    void showStudioPage(long studioId);

    void backToList();

    void sendSelectedOrderResult(Long id);
}
