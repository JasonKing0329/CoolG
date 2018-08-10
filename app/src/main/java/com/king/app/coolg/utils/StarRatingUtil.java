package com.king.app.coolg.utils;

import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.widget.TextView;

import com.king.app.coolg.R;
import com.king.app.gdb.data.entity.StarRating;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/5/9 10:46
 */
public class StarRatingUtil {

    public static final String NON_RATING = "NR";
    public static final String RATING_E = "E";
    public static final String RATING_EP = "E+";
    public static final String RATING_D = "D";
    public static final String RATING_DP = "D+";
    public static final String RATING_C = "C";
    public static final String RATING_CP = "C+";
    public static final String RATING_B = "B";
    public static final String RATING_BP = "B+";
    public static final String RATING_A = "A";
    public static final String RATING_AP = "A+";

    public static final float RATING_VALUE_CP = 2.6f;

    private static String[] rateValues = new String[] {
            NON_RATING, RATING_E, RATING_EP, RATING_D, RATING_DP, RATING_C, RATING_CP, RATING_B, RATING_BP, RATING_A, RATING_AP
    };

    private static float[] rateFactors = new float[] {
            0, 0.6f, 1.1f, 1.6f, 2.1f, RATING_VALUE_CP, 3.1f, 3.6f, 4.1f, 4.6f
    };

    public static String getRatingValue(float rating) {
        if (rating == 0) {
            return NON_RATING;
        }
        for (int i = 0; i < rateFactors.length; i ++) {
            if (rating <= rateFactors[i]) {
                return rateValues[i];
            }
        }
        return rateValues[rateValues.length - 1];
    }

    public static void updateRatingColor(TextView view, StarRating rating) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        int colorId = getRatingColorRes(rating == null ? 0:rating.getComplex());
        int color = view.getResources().getColor(colorId);
        drawable.setColor(color);
        view.setBackground(drawable);
    }

    public static int getRatingColor(float rating, Resources resources) {
        return resources.getColor(getRatingColorRes(rating));
    }

    public static int getRatingColorRes(float rating) {
        int colorId;
        if (rating == 0) {
            colorId = R.color.rating_nr;
        }
        else {
            switch (getRatingValue(rating)) {
                case RATING_E:
                    colorId = R.color.rating_e;
                    break;
                case RATING_EP:
                    colorId = R.color.rating_ep;
                    break;
                case RATING_D:
                    colorId = R.color.rating_d;
                    break;
                case RATING_DP:
                    colorId = R.color.rating_dp;
                    break;
                case RATING_C:
                    colorId = R.color.rating_cp;
                    break;
                case RATING_CP:
                    colorId = R.color.rating_cp;
                    break;
                case RATING_B:
                    colorId = R.color.rating_bp;
                    break;
                case RATING_BP:
                    colorId = R.color.rating_bp;
                    break;
                case RATING_A:
                    colorId = R.color.rating_ap;
                    break;
                case RATING_AP:
                    colorId = R.color.rating_ap;
                    break;
                default:
                    colorId = R.color.rating_nr;
                    break;
            }
        }
        return colorId;
    }
}
