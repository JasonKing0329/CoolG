package com.king.app.coolg.model.comparator;

import com.king.app.coolg.conf.RatingType;
import com.king.app.coolg.phone.star.list.StarProxy;

import java.util.Comparator;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:36
 */
public class StarRatingComparator implements Comparator<StarProxy> {

    private RatingType type;

    public StarRatingComparator(RatingType type) {
        this.type = type;
    }

    private float getCompareValue(StarProxy proxy) {
        float result = 0;
        try {
            switch (type) {
                case COMPLEX:
                    result = proxy.getStar().getRatings().get(0).getComplex();
                    break;
                case FACE:
                    result = proxy.getStar().getRatings().get(0).getFace();
                    break;
                case BODY:
                    result = proxy.getStar().getRatings().get(0).getBody();
                    break;
                case DK:
                    result = proxy.getStar().getRatings().get(0).getDk();
                    break;
                case SEXUALITY:
                    result = proxy.getStar().getRatings().get(0).getSexuality();
                    break;
                case PASSION:
                    result = proxy.getStar().getRatings().get(0).getPassion();
                    break;
                case VIDEO:
                    result = proxy.getStar().getRatings().get(0).getVideo();
                    break;
            }
        } catch (Exception e) {}
        return result;
    }

    @Override
    public int compare(StarProxy l, StarProxy r) {
        if (l == null || r == null) {
            return 0;
        }
        float left = getCompareValue(l);
        float right = getCompareValue(r);

        if (right - left < 0) {
            return -1;
        }
        else if (right - left > 0) {
            return 1;
        }
        else {
            // if same, then compare name and order by name asc
            return l.getStar().getName().toLowerCase().compareTo(r.getStar().getName().toLowerCase());
        }
    }
}