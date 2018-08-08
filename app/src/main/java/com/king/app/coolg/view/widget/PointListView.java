package com.king.app.coolg.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ScreenUtils;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/8 16:26
 */
public class PointListView extends View {

    private Paint mPaint = new Paint();

    // 设置或默认point大小
    private int mPointSize;
    // 实际point大小
    private int mPointRealSize;
    private int mPointMargin;
    private int mTextSize;
    private boolean mResizeWhenOver;

    private PointAdapter adapter;

    public PointListView(Context context) {
        super(context);
        init(null);
    }

    public PointListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs == null) {
            mTextSize = ScreenUtils.dp2px(12);
            mPointSize = ScreenUtils.dp2px(100);
            mPointMargin = ScreenUtils.dp2px(10);
            mResizeWhenOver = true;
        } else {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PointListView);
            mTextSize = a.getDimensionPixelSize(R.styleable.PointListView_pointTextSize, ScreenUtils.dp2px(14));
            mPointSize = a.getDimensionPixelSize(R.styleable.PointListView_pointSize, ScreenUtils.dp2px(100));
            mPointMargin = a.getDimensionPixelSize(R.styleable.PointListView_pointMargin, ScreenUtils.dp2px(10));
            mResizeWhenOver = a.getBoolean(R.styleable.PointListView_resizeWhenOver, true);
        }
    }

    public void setAdapter(PointAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int minimumWidth = getSuggestedMinimumWidth();
        int minimumHeight = getSuggestedMinimumHeight();
        DebugLog.e("---minimumWidth = " + minimumWidth + "");
        DebugLog.e("---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * specMode判断控件设置的layout_width
     * 1. 本view layout_width指定为固定值，specMode=固定值
     * 2. 本view嵌套在HorizontalScrollView中，HorizontalScrollView作用于横向滚动
     * --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=UNSPECIFIED
     * 为支持嵌入HorizontalScrollView滚动视图，在UNSPECIFIED里计算本view应该有的宽度
     * 3. 本view嵌套在其他没有横向滚动功能的ViewGroup中
     * --> ViewGroup宽度已知（指定过大小，或match_parent，parent已知大小，比如整个屏幕）
     * --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=AT_MOST
     * 所以这里选择在AT_MOST也运用本view应该有的宽度，也可以改为运用parent的宽度
     * --> ViewGroup宽度未知（不是说设置为wrap_content就是未知，而是比如嵌套在HorizontalScrollView中，导致ViewGroup的宽度也未知）
     * --> 同第2条
     * <p>
     * measureHeight同理，考虑layout_height与是否嵌入ScrollView
     *
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                if (adapter != null) {
                    defaultWidth = getPaddingLeft() + getPaddingRight()
                            + mPointSize * adapter.getItemCount()
                            + mPointMargin * (adapter.getItemCount() - 1);
                }
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                if (adapter != null) {
                    defaultWidth = getPaddingLeft() + getPaddingRight()
                            + mPointSize * adapter.getItemCount()
                            + mPointMargin * (adapter.getItemCount() - 1);
                }
        }
        DebugLog.e("---defaultWidth = " + defaultWidth + "");
        createPointRealSize();
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                if (adapter != null) {
                    defaultHeight = getPaddingTop() + getPaddingBottom() + mPointSize;
                }
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                DebugLog.e("---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
//                defaultHeight = Math.max(defaultHeight, specSize);
                DebugLog.e("---speSize = UNSPECIFIED");
                if (adapter != null) {
                    defaultHeight = getPaddingTop() + getPaddingBottom() + mPointSize;
                }
                break;
        }
        DebugLog.e("---defaultHeight = " + defaultHeight + "");
        return defaultHeight;
    }

    private void createPointRealSize() {
        mPointRealSize = mPointSize;
        // 超出边界重新计算point大小
        if (mResizeWhenOver && adapter != null) {
            int extraWidth = getPaddingLeft() + getPaddingRight() + mPointMargin * (adapter.getItemCount() - 1);
            if (mPointSize * adapter.getItemCount() + extraWidth > getWidth()) {
                mPointRealSize = (getWidth() - extraWidth) / adapter.getItemCount();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (adapter != null) {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                drawItem(canvas, i);
            }
        }
        super.onDraw(canvas);
    }

    private void drawItem(Canvas canvas, int i) {
        RectF rectF = new RectF();
        rectF.left = i * mPointRealSize + mPointMargin * i;
        rectF.top = 0;
        rectF.right = rectF.left + mPointRealSize;
        rectF.bottom = mPointRealSize;
        mPaint.setColor(adapter.getPointColor(i));
        canvas.drawOval(rectF, mPaint);

        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(mTextSize);
        textPaint.setColor(adapter.getTextColor(i));
        textPaint.setStyle(Paint.Style.FILL);
        Point point = new Point((int) rectF.centerX(), (int) rectF.centerY());

        textCenter(adapter.getText(i), textPaint, canvas, point, mPointRealSize
            , Layout.Alignment.ALIGN_CENTER, 1.5f ,0f, false);
    }

    private void textCenter(String string, TextPaint textPaint, Canvas canvas, Point point, int width, Layout.Alignment align, float spacingmult, float spacingadd, boolean includepad) {
        StaticLayout staticLayout = new StaticLayout(string, textPaint, width, align, spacingmult, spacingadd, includepad);
        canvas.save();
        canvas.translate(-staticLayout.getWidth() / 2 + point.x, -staticLayout.getHeight() / 2 + point.y);
        staticLayout.draw(canvas);
        canvas.restore();
    }

}
