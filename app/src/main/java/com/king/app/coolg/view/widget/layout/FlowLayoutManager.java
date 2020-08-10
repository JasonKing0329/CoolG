package com.king.app.coolg.view.widget.layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.coolg.utils.DebugLog;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/6 16:43
 */
public class FlowLayoutManager extends RecyclerView.LayoutManager {

    /**
     * 所有children的整体偏移量
     */
    private int mOffsetY;

    /**
     * 全部children的高度，用于判断滑动到底部
     */
    private int totalHeight;

    private class Row {
        int index;
        int height;
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        // //没有Item可布局，就回收全部临时缓存 (参考LinearLayoutManager)
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            return;
        }
        //暂时分离和回收全部有效的Item（必须调用）
        detachAndScrapAttachedViews(recycler);

        // 对每个子view开始布局（考虑滑动与回收）
        layoutChildren(recycler, state, 0);
    }

    /**
     * 只实现vertical的效果，这里直接为true就行
     * @return
     */
    @Override
    public boolean canScrollVertically() {
        return true;
    }

    /**
     * 滑动事件处理
     * 不断进行子view的实时布局，并真正处理回收情况
     * @param dy 大于0向上滑动，小于0向下滑动。注意dy是每一次微小滑动的位移，并非总位移
     * @param recycler
     * @param state
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        //分离和临时回收
        detachAndScrapAttachedViews(recycler);

        // 根据滑动位移计算当前整体偏移量
        computeOffset(dy);

        // 对所有子view进行布局
        layoutChildren(recycler, state, dy);
        return dy;
    }

    /**
     * dy是每一次微小滑动的位移，总的偏移量要叠加计算，用mOffsetY记录
     * @param dy
     */
    private void computeOffset(int dy) {
        // 子控件总高度小于recyclerView实际高度，不需要滑动
        if (totalHeight <= getHeight()) {
            mOffsetY = 0;
        }
        else {
            int targetY = mOffsetY - dy;
            // 即将滑动到顶端
            if (targetY > 0) {
                mOffsetY = 0;
            }
            // 即将滑动到底端
            else if (getHeight() - targetY > totalHeight) {
                mOffsetY = getHeight() - totalHeight;
            }
            else {
                mOffsetY = targetY;
            }
        }
    }

    /**
     * 1.对于每一个child，add,measure,layout(addView, measureChildWithMargins, layoutDecoratedWithMargins)必须依次执行
     * 原先设想可以先对所有children进行一次遍历只add和measure，最后在遍历一次进行布局or回收，但是这样就导致视图最终无法呈现
     * 2.对于视线外的children进行recycle，但params.isItemRemoved不管用，即便执行了recycleAndRemoveView，仍然返回false，导致每一次滚动都会执行视线外所有child的回收
     * 这在item很多的情况下经验证，滑动起来会比较卡顿。目前不知道怎么解决
     * @param recycler
     * @param state
     * @param dy
     */
    private void layoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int itemCount = state.getItemCount();
        // 本类为线性布局，记录当前item的top
        int y = mOffsetY;
        int x = 0;

        Row row = new Row();
        for (int i = 0; i < itemCount; i ++) {
            // 获取子view，方法内部会自行判断是从缓存中取还是新对象
            View item = recycler.getViewForPosition(i);
            addView(item);
            // 父类方法，考虑margin时调用的计算子类宽高
            measureChildWithMargins(item, 0, 0);
            // 获取子view的宽高
            int width = getDecoratedMeasuredWidth(item);
            int height = getDecoratedMeasuredHeight(item);
            DebugLog.e("" + i + ", width=" + width + ", height=" + height);

            // 视图待布局的位置
            Rect rect = new Rect(x, y, x + width, y + height);
            // 当前行越界，换行
            if (rect.right > getWidth()) {
                y += row.height;
                x = 0;
                rect = new Rect(x, y, x + width, y + height);
            }
            // 当前行
            else {
                // 修正当前行最大高度
                if (height > row.height) {
                    row.height = height;
                }
            }

            // 只添加视线范围内的view，并回收视线范围外的view
            if (rect.bottom < 0 || rect.top > getHeight()) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) item.getLayoutParams();
                if (!params.isItemRemoved()) {
//                    DebugLog.e("removeAndRecycleView " + i);
//                    removeAndRecycleView(item, recycler);
                }
            }
            else {
                DebugLog.e("layoutDecoratedWithMargins " + rect.toString());
                layoutDecoratedWithMargins(item, rect.left, rect.top, rect.right, rect.bottom);
            }

            // 预先给出下一个item应该出现的位置（不考虑其宽高）
            x += width;
        }
        // 记录全部子view的总高度，用于判断是否滑动到底部
        totalHeight = y - mOffsetY;
    }

}
