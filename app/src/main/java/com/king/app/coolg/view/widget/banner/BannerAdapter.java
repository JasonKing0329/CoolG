package com.king.app.coolg.view.widget.banner;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.coolg.utils.DebugLog;

import java.util.ArrayList;

public abstract class BannerAdapter extends PagerAdapter
        implements ViewPager.OnPageChangeListener, View.OnClickListener {

    private ViewPager viewPager;

    private final static int PAGE = 0x03;
    private final static int INDEX = 1;
    private final static int START_INDEX = 0;

    private ArrayList<View> views;
    //记录真正的位置
    private int currentPage;

    private ItemClickListener itemClickListener;

    private OnSetPageListener onSetPageListener;

    public BannerAdapter(ViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public void init() {
        if (getItemCount() == 0) return;
        initView(false);
        setDataToView();
    }

    /**
     * 不能直接拿viewPager.getContext()，拿到的类型是ContextThemeWrapper
     * @return
     */
    protected Context getContext() {
        return getContext(viewPager);
    }

    /**
     * try get host activity from view.
     * views hosted on floating window like dialog and toast will sure return null.
     * @return host activity; or null if not available
     */
    public static Context getContext(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public abstract int getItemCount();

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnSetPageListener(OnSetPageListener onSetPageListener) {
        this.onSetPageListener = onSetPageListener;
    }

    public void nextPage() {
        if (getItemCount() == 0) return;
        //因为位置始终为1 那么下一页就始终为2
        viewPager.setCurrentItem(2, true);
    }

    /**
     * 初始化图形控件
     * 为image填充数据
     */
    private void initView(boolean isScale) {
        views = new ArrayList<>();
        if (getItemCount() == 1) {
            views.add(onCreateView());
            return;
        }
        for (int i = 0; i < PAGE; i++) {
            views.add(onCreateView());
        }
    }

    protected abstract View onCreateView();

    private void setDataToView() {
        int size = getItemCount();
        if (size == 1) {
            onBindItem(0, views.get(0));
            return;
        }
        for (int i = 0; i < PAGE; i++) {
            int index = 0;
            if (0 == i) {
                index = size - 1;
            } else {
                index = i - 1;
            }
            onBindItem(index, views.get(i));
        }
    }

    protected abstract void onBindItem(int position, View imageView);

    public void showIndex(int index) {
        if (getItemCount() == 0) return;
        if (index == getItemCount()) {
            currentPage = 0;
        } else {
            currentPage = index;
        }
        if (getItemCount() == 1) {
            onBindItem(0, views.get(0));
        } else {
            indexChange();
        }
    }

    @Override
    public int getCount() {
        return (null == views) ? 0 : views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        DebugLog.e("" + position);
        View view = views.get(position);
        if (null != view.getParent()) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            viewGroup.removeView(view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        DebugLog.e("" + position);
        container.removeView((View) object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (getItemCount() == 1) {
            return;
        }
        if (0 != positionOffset) return;

        if (position == INDEX) {
            return;
        }
        DebugLog.e("position " + position);
        if (position > INDEX) {
            currentPage++;
        } else {
            currentPage--;
        }
        if (currentPage == -INDEX) {
            currentPage = getItemCount() - INDEX;
        } else if (currentPage == getItemCount()) {
            currentPage = START_INDEX;
        }
        DebugLog.e("currentPage " + currentPage);
        indexChange();
    }

    private void indexChange() {
        if (currentPage == START_INDEX) {
            onBindItem(getItemCount() - 1, views.get(0));
        } else {
            onBindItem(currentPage - 1, views.get(0));
        }
        onBindItem(currentPage, views.get(1));
        if (currentPage == getItemCount() - 1) {
            onBindItem(0, views.get(2));
        } else {
            if (currentPage == 0 && getItemCount() == 2) {
                onBindItem(getItemCount() - 1, views.get(2));
            } else {
                onBindItem(currentPage + 1, views.get(2));
            }
        }
        viewPager.setCurrentItem(1, false);
        DebugLog.e("currentPage=" + currentPage);
        if (onSetPageListener != null) {
            onSetPageListener.onSetPage(currentPage);
        }
    }

    @Override
    public void onPageSelected(int position) {
        DebugLog.e(String.valueOf(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        DebugLog.e("state=" + state);
        switch (state) {
            case ViewPager.SCROLL_STATE_IDLE:
                break;
            case ViewPager.SCROLL_STATE_DRAGGING:
                break;
            case ViewPager.SCROLL_STATE_SETTLING:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (null != itemClickListener) itemClickListener.onItemClick(currentPage);
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public interface ItemClickListener {
        void onItemClick(int index);
    }

    public interface OnSetPageListener {
        void onSetPage(int position);
    }
}