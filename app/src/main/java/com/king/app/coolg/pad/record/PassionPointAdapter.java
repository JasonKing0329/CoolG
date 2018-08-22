package com.king.app.coolg.pad.record;

import android.graphics.Color;
import android.support.v7.graphics.Palette;

import com.king.app.coolg.phone.record.PassionPoint;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.ListUtil;
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
    private List<Palette.Swatch> swatches;

    public void setList(List<PassionPoint> list) {
        this.list = list;
    }

    public void setSwatches(List<Palette.Swatch> swatches) {
        this.swatches = swatches;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    @Override
    public int getPointColor(int position) {
        if (ListUtil.isEmpty(swatches)) {
            return ColorUtil.randomWhiteTextBgColor();
        }
        else {
            return swatches.get(position % swatches.size()).getRgb();
        }
    }

    @Override
    public int getTextColor(int position) {
        if (ListUtil.isEmpty(swatches)) {
            return Color.WHITE;
        }
        else {
            return swatches.get(position % swatches.size()).getBodyTextColor();
        }
    }

    @Override
    public String getText(int position) {
        return list.get(position).getKey() + "\n" + list.get(position).getContent();
    }
}
