package com.king.app.coolg.phone.record;

import android.graphics.Color;

import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.view.widget.PointAdapter;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 17:26
 */
public class PassionPointAdapter extends PointAdapter {

    private List<PassionPoint> list;

    public void setList(List<PassionPoint> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    @Override
    public int getPointColor(int position) {
        return ColorUtil.randomWhiteTextBgColor();
    }

    @Override
    public int getTextColor(int position) {
        return Color.WHITE;
    }

    @Override
    public String getText(int position) {
        return list.get(position).getKey() + "\n" + list.get(position).getContent();
    }
}
