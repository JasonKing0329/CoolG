package com.king.app.coolg.view.transformer;

import android.view.View;

import com.allure.lbanners.transformer.LMPageTransformer;
import com.nineoldandroids.view.ViewHelper;

/**
 * Desc: 参考com.allure.lbanners.transformer.FlipPageTransformer
 *
 * @author：Jing Yang
 * @date: 2019/2/28 13:48
 */
public class FlipPageTransformer extends LMPageTransformer {
    private static final float ROTATION = 180.0f;

    @Override
    public void scrollInvisible(View view, float position) {
    }

    @Override
    public void scrollLeft(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);
        float rotation = (ROTATION * position);
        ViewHelper.setRotationY(view, rotation);

        if (position > -0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void scrollRight(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);
        float rotation = (ROTATION * position);
        ViewHelper.setRotationY(view, rotation);

        if (position < 0.5) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.INVISIBLE);
        }
    }


}