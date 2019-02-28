package com.king.app.coolg.view.widget.banner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;

import com.king.app.coolg.R;
import com.king.app.coolg.utils.DebugLog;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Administrator on 2018/8/25 0025.
 */

public class CoolBanner extends ViewPager {

    private BannerAdapter adapter;

    private CompositeDisposable compositeDisposable;

    private int duration = 5000;

    private boolean enableSwitch = true;

    private int timeCount;

    public CoolBanner(@NonNull Context context) {
        super(context);
        init(null);
    }

    public CoolBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        compositeDisposable = new CompositeDisposable();
        if (attrs != null) {
            TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs
                    , R.styleable.CoolBanner, 0, 0);
            duration = a.getInteger(R.styleable.CoolBanner_switchDuration, 5000);
        }
        try {
            // 通过class文件获取mScroller属性
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            FixedSpeedScroller mScroller = new FixedSpeedScroller(getContext(), new AccelerateInterpolator());
            mScroller.setScrollDuration(500);
            mField.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setBannerAdapter(BannerAdapter adapter) {
        removeOnPageChangeListener(adapter);
        this.adapter = adapter;
        removeAllViews();
        if (adapter != null) {
            adapter.init();
        }
        setAdapter(adapter);
        addOnPageChangeListener(adapter);
    }

    public void startAutoPlay() {
        timeCount = 0;
        compositeDisposable.clear();
        if (adapter != null && adapter.getItemCount() > 1) {
            Disposable disposable = Observable.interval(1000, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        timeCount += 1000;
                        DebugLog.e("timeCount=" + timeCount);
                        if (timeCount >= duration) {
                            timeCount = 0;
                            adapter.nextPage();
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

    public int getPosition() {
        return adapter == null ? 0:adapter.getCurrentPage();
    }

    public void stopAutoPlay() {
        timeCount = 0;
        compositeDisposable.clear();
    }

    public void setEnableSwitch(boolean enableSwitch) {
        this.enableSwitch = enableSwitch;
    }

    public boolean isEnableSwitch() {
        return enableSwitch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (enableSwitch) {
            return super.onInterceptTouchEvent(ev);
        }
        else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enableSwitch) {
            return super.onTouchEvent(ev);
        }
        else {
            return false;
        }
    }

    /**
     * 当有touch事件时，重置计时
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 必须在dispatchTouchEvent处理，因为这里onTouchEvent里接收不到ACTION_DOWN事件
        if (enableSwitch) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    resetTimer();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 暂停切换计时
     */
    private void resetTimer() {
        timeCount = 0;
    }

}
