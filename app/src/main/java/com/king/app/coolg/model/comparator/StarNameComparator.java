package com.king.app.coolg.model.comparator;

import com.king.app.coolg.phone.star.list.StarProxy;

import java.util.Comparator;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:38
 */
public class StarNameComparator implements Comparator<StarProxy> {

    @Override
    public int compare(StarProxy l, StarProxy r) {
        if (l == null || r == null) {
            return 0;
        }

        return l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
    }
}