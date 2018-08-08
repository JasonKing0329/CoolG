package com.king.app.coolg.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.king.app.coolg.R;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;

import java.util.List;

/**
 * 描述: 充满全屏宽度的横向列表，列表item为纵向布局圆形背景TextView+普通TextView，圆形背景TextView自适应大小
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/10 16:40
 */
public class PointDescLayout extends LinearLayout {

    private int mPointSize;
    private int mPointMargin;
    private int mTextSize;
    private boolean mResizeWhenOver;

    private Animation itemAnimation;
    private boolean bInstantShow = true;

    public PointDescLayout(Context context) {
        super(context);
        init(null);
    }

    public PointDescLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mTextSize = ScreenUtils.dp2px(14);
            mPointSize = ScreenUtils.dp2px(100);
            mPointMargin = ScreenUtils.dp2px(10);
            mResizeWhenOver = true;
        }
        else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PointDescLayout);
            mTextSize = a.getDimensionPixelSize(R.styleable.PointDescLayout_descTextSize, ScreenUtils.dp2px(14));
            mPointSize = a.getDimensionPixelSize(R.styleable.PointDescLayout_pointSize, ScreenUtils.dp2px(100));
            mPointMargin = a.getDimensionPixelSize(R.styleable.PointDescLayout_pointMargin, ScreenUtils.dp2px(10));
            mResizeWhenOver = a.getBoolean(R.styleable.PointDescLayout_resizeWhenOver, true);
        }
    }

    public void addPoints(List<String> keys, List<String> contents) {

        if (keys.size() == 0) {
            return;
        }

        int itemWidth = mPointSize;
        boolean needResize = mResizeWhenOver && mPointSize * keys.size() + mPointMargin * (keys.size() - 1) > getWidth();
        if (needResize) {
            itemWidth = (getWidth() - mPointMargin * (keys.size() - 1)) / keys.size();
        }

        for (int i = 0; i < keys.size(); i ++) {
            LinearLayout group = new LinearLayout(getContext());
            group.setOrientation(VERTICAL);
            group.setGravity(Gravity.CENTER_HORIZONTAL);
            if (!bInstantShow) {
                group.setVisibility(INVISIBLE);
            }

            LayoutParams groupParams;
            if (needResize) {
                groupParams = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                groupParams.weight = 1;
            }
            else {
                groupParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            groupParams.leftMargin = mPointMargin;
            addView(group, groupParams);

            TextView tvKey = new TextView(getContext());
            tvKey.setText(keys.get(i) + "\n" + contents.get(i));
            tvKey.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            tvKey.setTextColor(Color.WHITE);
            tvKey.setGravity(Gravity.CENTER);
            tvKey.setBackground(getContext().getResources().getDrawable(R.drawable.shape_oval_common));
            updateBackground(tvKey);
            LayoutParams keyParams = new LayoutParams(itemWidth, itemWidth);
            group.addView(tvKey, keyParams);

//            TextView tvContent = new TextView(getContext());
//            tvContent.setText(contents.get(i));
//            LayoutParams contentParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            group.addView(tvContent, contentParams);
        }
    }

    /**
     * 随机生成适用于白色文字的背景
     * @param view
     */
    private void updateBackground(View view) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(ColorUtil.randomWhiteTextBgColor());
    }

    private void updateBackground(View view, int color) {
        GradientDrawable drawable = (GradientDrawable) view.getBackground();
        drawable.setColor(color);
    }

    public void setSwatches(List<Palette.Swatch> swatches) {
        if (getChildCount() > 0 && !ListUtil.isEmpty(swatches)) {
            int swatchIndex = 0;
            for (int i = 0; i < getChildCount(); i ++) {
                if (swatchIndex >= swatches.size()) {
                    swatchIndex = 0;
                }
                TextView child = (TextView) ((ViewGroup) getChildAt(i)).getChildAt(0);
                updateBackground(child, swatches.get(swatchIndex).getRgb());
                child.setTextColor(swatches.get(swatchIndex).getTitleTextColor());
                swatchIndex ++;
            }
        }
    }

    public void disableInstantShow() {
        bInstantShow = false;
    }

    public void registerItemAnimation(Animation itemAnimation) {
        this.itemAnimation = itemAnimation;
    }

    public void showItems(long delay, final boolean useAnim) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showItems(useAnim);
            }
        }, delay);
    }

    public void showItems(boolean useAnim) {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i ++) {
                getChildAt(i).setVisibility(VISIBLE);
                if (useAnim && itemAnimation != null) {
                    getChildAt(i).startAnimation(itemAnimation);
                }
            }
        }
    }
}
