package com.king.app.coolg.view.widget.layout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.king.app.coolg.utils.DebugLog;

/**
 * Desc: 用于学习并记录的LayoutManager，实现简单的linear vertical效果
 * 目前本类只适用于item的宽高都是确定值，wrap的情况还无法适配，原因在layoutChildren方法出详细解释
 *
 * @author：Jing Yang
 * @date: 2020/8/4 14:46
 */
public class LearnLayoutManager extends RecyclerView.LayoutManager {

    /**
     * 所有children的整体偏移量
     */
    private int mOffsetY;

    /**
     * 全部children的高度，用于判断滑动到底部
     */
    private int totalHeight;

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    /**
     * 入口
     * 1 在RecyclerView初始化时，会被调用两次。
     * 2 在调用adapter.notifyDataSetChanged()时，会被调用。
     * 3 在调用setAdapter替换Adapter时,会被调用。
     * 4 在RecyclerView执行动画时，它也会被调用。
     * 即RecyclerView 初始化 、 数据源改变时 都会被调用。
     * @param recycler
     * @param state
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        DebugLog.e("");
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
     * 本类不需要考虑horizontal效果
     * @return
     */
    @Override
    public boolean canScrollHorizontally() {
        return super.canScrollHorizontally();
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
     * 对子view进行添加、布局、回收的工作
     * 目前只对item的宽高值确定的情况有效，原因：
     * 1.每个item layout的top值是基于整体偏移量mOffsetY与前面item的高度决定的，
     * 即 top(i) = mOffsetY - (height(i-1) + height(i - 2) ... + height(0))
     * mOffsetY是scrollVerticallyBy过程中dy的累加值，并控制了顶部底部不再滑动。是整个列表的偏移值
     * 而在item的高度属性是wrap_content的时候，第一次onLayoutChildren，获得的item，其measure height=0，(后来经证明并不是因为控件设置了wrap_content
     * ，像是TextView如果设置的wrap_content，这里也能计算出实际height，用于验证的ImageView设置了height是wrap_content，并且设置了adjustViewBounds属性，才导致measure height=0)
     * 这就导致了所有item全部会被进行addView，只有addView之后下一次onLayoutChildren，才会得出大于0的height。而每addView一次，便会激活一次onLayoutChildren。
     * 到这里为止对于整体偏移量以及滑动处理都还没有问题，问题出现在当第一个item被滑出视野时，
     * 即i=0被回收后，下一次scrollVerticallyBy触发layoutChildren，造成height(i)即height(0)又等于0了，这时通过上面的公式可知：
     * top(i, i>0)之后所有的取值都不对了，因为mOffsetY仍然是整体偏移量，即第0个item的偏移量，而height(0)变为0了
     *
     * 2. 1中提到导致了所有item全部会被进行addView，只有addView之后下一次onLayoutChildren，才会得出大于0的height。而每addView一次，便会激活一次onLayoutChildren。
     * 这样会造成addView了所有children，虽然后续有recycle，但仍然不应该一次全部addView所有
     *
     * @param recycler
     * @param state
     * @param dy
     */
    private void layoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state, int dy) {
        int itemCount = state.getItemCount();
        int x = 0;
        // 本类为线性布局，记录当前item的top
        int y = mOffsetY;
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

            // 只添加视线范围内的view，并回收视线范围外的view
            if (rect.bottom < 0 || rect.top > getHeight()) {
                DebugLog.e("removeAndRecycleView " + i);
                removeAndRecycleView(item, recycler);
            }
            // 当布局采用了wrap方式，height还没有被计算出来时，不添加
            else {
//            else if (height > 0) {
                DebugLog.e("layoutDecoratedWithMargins " + rect.toString());
                layoutDecoratedWithMargins(item, rect.left, rect.top, rect.right, rect.bottom);
            }

            // 修正下一个子view的top
            y += height;
        }
        // 记录全部子view的总高度，用于判断是否滑动到底部
        totalHeight = y - mOffsetY;
    }
}
