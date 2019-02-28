package com.king.app.coolg.view.transformer;

import android.view.View;

import com.allure.lbanners.transformer.LMPageTransformer;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by lm on 2016/7/8.
 */
public class StackPageTransformer extends LMPageTransformer {



    @Override
    public void scrollInvisible(View view, float position) {

    }

    @Override
    public void scrollLeft(View view, float position) {

    }

    @Override
    public void scrollRight(View view, float position) {
        ViewHelper.setTranslationX(view, -view.getWidth() * position);

    }
}