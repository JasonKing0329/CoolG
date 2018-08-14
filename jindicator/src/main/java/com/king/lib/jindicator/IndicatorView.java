package com.king.lib.jindicator;

import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class IndicatorView extends HorizontalScrollView
        implements View.OnClickListener, AnimationListener {

    private int mTextColor;
    private int mTextSize;
    private int mIndicatorColor;
    private int mIndicatorColorPressed;
    private int mEnterAnim;
    private int mExitAnim;
    private int mIndicatorHeight;

    private PathIndicatorListener indicatorListener;
    private FrameLayout container;
    private List<IndicatorNode> pathList;

    private Animation appearAnim, disappearAnim;

    private boolean isDisappearing;

    public IndicatorView(Context context) {
        super(context);
        init(null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IndicatorView(Context context, AttributeSet attrs,
                                 int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        container = new FrameLayout(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        container.setLayoutParams(params);
        addView(container);

        if (attrs == null) {
            mTextColor = Color.WHITE;
            mTextSize = DensityUtil.dp2px(getContext(), 14);
            mIndicatorColor = getContext().getResources().getColor(R.color.colorPrimary);
            mIndicatorColorPressed = getContext().getResources().getColor(R.color.colorAccent);
            mEnterAnim = R.anim.path_indicator_in;
            mExitAnim = R.anim.path_indicator_out;
            mIndicatorHeight = getContext().getResources().getDimensionPixelSize(R.dimen.indicator_height);
        }
        else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.IndicatorView);
            mTextColor = a.getColor(R.styleable.IndicatorView_indicatorTextColor, Color.WHITE);
            mTextSize = a.getDimensionPixelSize(R.styleable.IndicatorView_indicatorTextSize, DensityUtil.dp2px(getContext(), 14));
            mIndicatorColor = a.getColor(R.styleable.IndicatorView_indicatorColor
                    , getContext().getResources().getColor(R.color.colorPrimary));
            mIndicatorColorPressed = a.getColor(R.styleable.IndicatorView_indicatorColorPressed
                    , getContext().getResources().getColor(R.color.colorAccent));
            mEnterAnim = a.getResourceId(R.styleable.IndicatorView_indicatorEnterAnim, R.anim.path_indicator_in);
            mExitAnim = a.getResourceId(R.styleable.IndicatorView_indicatorExitAnim, R.anim.path_indicator_out);
            mIndicatorHeight = a.getDimensionPixelSize(R.styleable.IndicatorView_indicatorHeight
                    , getContext().getResources().getDimensionPixelSize(R.dimen.indicator_height));
        }

        setHorizontalScrollBarEnabled(false);
    }

    public void setPathIndicatorListener(PathIndicatorListener listener) {
        this.indicatorListener = listener;
    }

    public void create(List<IndicatorNode> pathList) {
        this.pathList = pathList;
        container.removeAllViews();
        if (pathList != null) {
            for (int i = 0; i < pathList.size(); i++) {
                addIndicator(pathList.get(i));
            }
        }
    }

    public List<IndicatorNode> getPathList() {

        if (pathList != null) {
            for (int i = 0; i < pathList.size(); i++) {
                IndicatorNode node = pathList.get(i);
                int indexInContainer = container.getChildCount() - 1 - node.getIndex();
                node.setIndexInContainer(indexInContainer);
                node.setWidth(container.getChildAt(indexInContainer).getWidth());
            }
        }
        return pathList;
    }

    private void addIndicator(IndicatorNode node) {

        if (node.getName() == null) {
            String array[] = node.getPath().split("/");
            String name = array[array.length - 1];
            if (name.length() == 0) {
                name = array[array.length - 2];
            }
            node.setName(name);
        }

        final TextView textView = new TextView(getContext());
        FrameLayout.LayoutParams indicatorParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, mIndicatorHeight);
        if (node.getIndex() == 0) {
            node.setLeft(0);
        } else {
            MarginLayoutParams params = indicatorParams;
            if (node.getLeft() != -1) {
                params.leftMargin = node.getLeft();
            } else {
                View lastItem = container.getChildAt(0);
                params.leftMargin = lastItem.getLeft() + lastItem.getWidth() - DensityUtil.dp2px(getContext(), 35);
                node.setLeft(params.leftMargin);
            }
        }
        textView.setLayoutParams(indicatorParams);
        textView.setText(node.getName());
        textView.setTextColor(mTextColor);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(DensityUtil.dp2px(getContext(), 20), 0, DensityUtil.dp2px(getContext(), 32), 0);
        textView.setTag(node);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setOnClickListener(this);
        if (node.getIndex() == 0) {
            container.addView(textView);
        } else {
            container.addView(textView, 0);
        }

        textView.post(new Runnable() {
            @Override
            public void run() {
                setIndicatorBackgroud(textView);
            }
        });
    }

    private void setIndicatorBackgroud(TextView textView) {
        int width = textView.getWidth();
        int height = textView.getHeight();

        // 将.9的资源转换成bitmap
        // getUpdatedDrawable中是改变颜色的偏移量来达到改变颜色值的，所以用的底图必须是从0开始的才能达到target颜色指定的值
        NinePatchDrawable npd = (NinePatchDrawable) getResources().getDrawable(R.drawable.ind_000000);
        npd.setBounds(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        npd.draw(canvas);

        // 改变图片的颜色
        Drawable normal = getUpdatedDrawable(bitmap, mIndicatorColor);
        Drawable pressed = getUpdatedDrawable(bitmap, mIndicatorColorPressed);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[] { android.R.attr.state_pressed }, pressed);
        drawable.addState(new int[] {}, normal);
        textView.setBackgroundDrawable(drawable);
    }

    /**
     * http://blog.csdn.net/janice0529/article/details/49207939
     * 通过改变颜色分量来改变纯色图片的颜色
     * 要求bitmap的有色区域是纯黑色的
     * @param bitmap original bitmap
     * @param color target color
     * @return
     */
    private Drawable getUpdatedDrawable(Bitmap bitmap, int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0x00ff00) >> 8;
        int blue = (color & 0x0000ff);
        /**
         * 第一行决定红色 R
         第二行决定绿色 G
         第三行决定蓝色 B
         第四行决定了透明度 A
         第五列是颜色的偏移量
         */
        float[] src = new float[]{
                1, 0, 0, 0, red,
                0, 1, 0, 0, green,
                0, 0, 1, 0, blue,
                0, 0, 0, 1, 0};
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(src);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(src));
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(newBitmap);
        // 5.通过指定了RGBA矩阵的Paint把原图画到空白图片上
        canvas.drawBitmap(bitmap, new Matrix(), paint);
        return new BitmapDrawable(newBitmap);
    }

    public void addPath(String path) {
        if (pathList == null) {
            pathList = new ArrayList<>();
        }
        IndicatorNode node = new IndicatorNode();
        node.setPath(path);
        node.setIndex(pathList.size());
        pathList.add(node);
        addIndicator(node);

        container.getChildAt(0).startAnimation(getAppearAnimation());
    }

    private Animation getAppearAnimation() {
        if (appearAnim == null) {
            appearAnim = AnimationUtils.loadAnimation(getContext(), mEnterAnim);
        }
        return appearAnim;
    }

    private Animation getDisappearAnimation() {
        if (disappearAnim == null) {
            disappearAnim = AnimationUtils.loadAnimation(getContext(), mExitAnim);
            disappearAnim.setAnimationListener(this);
        }
        return disappearAnim;
    }


    @Override
    public void onClick(View view) {

        IndicatorNode node = (IndicatorNode) view.getTag();
        int indexInContainer = container.getChildCount() - 1 - node.getIndex();
        for (int i = indexInContainer - 1; i >= 0; i--) {
            container.removeViewAt(i);
        }
        for (int i = pathList.size() - 1; i >= node.getIndex() + 1; i--) {
            pathList.remove(i);
        }

        if (indicatorListener != null) {
            indicatorListener.onClickPath(node.getIndex(), node.getPath());
        }
    }

    public void backToUpper() {
        if (!isDisappearing) {
            isDisappearing = true;
            container.getChildAt(0).startAnimation(getDisappearAnimation());
        }
    }

    public boolean isBackable() {
        return !isDisappearing;
    }

    public boolean isRoot() {
        return container.getChildCount() <= 1;
    }

    @Override
    public void onAnimationEnd(Animation arg0) {
        //must remove it after animation end, otherwise the effect can't be seen
        container.getChildAt(0).post(new Runnable() {
            @Override
            public void run() {
                container.removeViewAt(0);
                pathList.remove(pathList.size() - 1);
                isDisappearing = false;
            }
        });
    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    public interface PathIndicatorListener {

        void onClickPath(int index, String path);
    }

}
