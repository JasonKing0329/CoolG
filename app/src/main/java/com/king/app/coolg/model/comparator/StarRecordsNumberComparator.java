package com.king.app.coolg.model.comparator;

import com.king.app.coolg.phone.star.list.StarProxy;

import java.util.Comparator;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:37
 */
public class StarRecordsNumberComparator implements Comparator<StarProxy> {

    @Override
    public int compare(StarProxy l, StarProxy r) {
        if (l == null || r == null) {
            return 0;
        }

        // order by record number desc
        int result = r.getStar().getRecords() - l.getStar().getRecords();
        // if same, then compare name and order by name asc
        if (result == 0) {
            result = l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
        }
        return result;
    }
}