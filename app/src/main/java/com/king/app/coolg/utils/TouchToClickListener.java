package com.king.app.coolg.utils;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * @description:
 * @author：Jing
 * @date: 2020/7/12 22:34
 */
public class TouchToClickListener {

    private View view;

    private long lastTime;
    private float lastX;
    private float lastY;

    private View.OnClickListener onClickListener;

    private View.OnLongClickListener onLongClickListener;

    public TouchToClickListener(View v) {
        this.view = v;
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTime = System.currentTimeMillis();
                        lastX = motionEvent.getX();
                        lastY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:
                        long time = System.currentTimeMillis();
                        float x = motionEvent.getX();
                        float y = motionEvent.getY();
                        long during = time - lastTime;
                        float xOffset = Math.abs(x - lastX);
                        float yOffset = Math.abs(y - lastY);
//                        DebugLog.e("during=" + during + ", xOffset=" + xOffset + ", yOffset=" + yOffset);
                        if (xOffset < 50 && yOffset < 50) {
                            if (during < 250) {
                                if (onClickListener != null) {
                                    onClickListener.onClick(view);
                                }
                            }
                            else if (during > 1000) {
                                if (onLongClickListener != null) {
                                    onLongClickListener.onLongClick(view);
                                }
                            }
                        }
                        break;
                }
                return false;
            }
        });
        if (view.getContext() instanceof LifecycleOwner) {
            ((LifecycleOwner) view.getContext()).getLifecycle().addObserver(new LifecycleObserver() {

                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                public void onDestroy() {
                    view = null;
                }
            });
        }
    }

    public void setOnClickListener(View.OnClickListener listener) {
        onClickListener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}
