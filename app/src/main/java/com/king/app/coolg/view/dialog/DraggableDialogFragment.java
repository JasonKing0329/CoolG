package com.king.app.coolg.view.dialog;

import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.king.app.coolg.R;
import com.king.app.coolg.databinding.DialogBaseBinding;
import com.king.app.coolg.utils.DebugLog;
import com.king.app.coolg.utils.ScreenUtils;

/**
 * 描述: 可拖拽移动的base dialog框架
 * <p/>作者：景阳
 * <p/>创建时间: 2017/7/20 11:45
 */
public class DraggableDialogFragment extends BaseDialogFragment implements DraggableHolder {

    private Point startPoint, touchPoint;

    private String title;

    private int backgroundColor;

    private boolean hideClose;

    private DraggableContentFragment contentFragment;

    private int maxHeight;

    private DialogBaseBinding binding;

    protected DialogInterface.OnDismissListener onDismissListener;

    @Override
    protected View onSubCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DialogBaseBinding.inflate(inflater);
        initView();
        return binding.getRoot();
    }

    protected void initView() {

        if (title != null) {
            binding.tvTitle.setText(title);
        }
        if (backgroundColor != 0) {
            GradientDrawable drawable = (GradientDrawable) binding.groupDialog.getBackground();
            drawable.setColor(backgroundColor);
        }
        if (hideClose) {
            binding.ivClose.setVisibility(View.GONE);
        }

        initDragParams();

        if (contentFragment != null) {
            contentFragment.setDialogHolder(this);
            replaceContentFragment(contentFragment, "ContentView");
        }

        binding.flFt.post(() -> {
            DebugLog.e("groupFtContent height=" + binding.flFt.getHeight());
            limitMaxHeight();
        });

        binding.ivClose.setOnClickListener(v -> dismissAllowingStateLoss());
    }

    protected void replaceContentFragment(DraggableContentFragment target, String tag) {
        if (target != null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.fl_ft, target, tag);
            ft.commit();
        }
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }

    private void limitMaxHeight() {
        int maxContentHeight = getMaxHeight();
        if (binding.flFt.getHeight() > maxContentHeight) {
            ViewGroup.LayoutParams params = binding.flFt.getLayoutParams();
            params.height = maxContentHeight;
            binding.flFt.setLayoutParams(params);
        }
    }

    /**
     * 子类可选择覆盖
     * @return
     */
    protected int getMaxHeight() {
        if (maxHeight != 0) {
            return maxHeight;
        }
        else {
            return ScreenUtils.getScreenHeight() * 3 / 5;
        }
    }

    private void initDragParams() {
        touchPoint = new Point();
        startPoint = new Point();
        binding.groupDialog.setOnTouchListener(new DialogTouchListener());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setHideClose(boolean hideClose) {
        this.hideClose = hideClose;
    }

    public void setContentFragment(DraggableContentFragment contentFragment) {
        this.contentFragment = contentFragment;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    @Override
    public View inflateToolbar(int layout) {
        View view = getLayoutInflater().inflate(layout, null);
        binding.flToolbar.addView(view);
        return view;
    }

    private class Point {
        float x;
        float y;
    }

    /**
     * move dialog
     */
    private class DialogTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getRawX();//
                    float y = event.getRawY();
                    startPoint.x = x;
                    startPoint.y = y;
                    DebugLog.d("ACTION_DOWN x=" + x + ", y=" + y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    x = event.getRawX();
                    y = event.getRawY();
                    touchPoint.x = x;
                    touchPoint.y = y;
                    float dx = touchPoint.x - startPoint.x;
                    float dy = touchPoint.y - startPoint.y;

                    move((int) dx, (int) dy);

                    startPoint.x = x;
                    startPoint.y = y;
                    break;
                case MotionEvent.ACTION_UP:
                    break;

                default:
                    break;
            }
            return true;
        }
    }

}
