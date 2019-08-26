package com.king.app.coolg.phone.studio.page;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;

/**
 * Desc: 混合模式布局适配器
 * 使用开源框架MultiViewAdapter
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:09
 */
public class StudioPageAdapter extends RecyclerAdapter {

    // 指定adapter关联的实体
    private DataListManager<String> headManager;
    private DataListManager<StarNumberItem> starNumberManager;
    private DataListManager<RecordProxy> recordManager;
    private DataListManager<StudioPageItem> infoManager;

    private HeaderAdapter headerAdapter;
    private StarNumAdapter starNumAdapter;
    private InfoAdapter infoAdapter;
    private RecordAdapter recordAdapter;

    private StudioPageItem pageItem;

    public StudioPageAdapter() {
        headManager = new DataListManager<>(this);
        starNumberManager = new DataListManager<>(this);
        recordManager = new DataListManager<>(this);
        infoManager = new DataListManager<>(this);

        // 指定混合模式的布局顺序
        // 这里是 标题栏-9宫格star-信息栏-record列表，标题栏直接使用了String
        addDataManager(headManager);
        addDataManager(starNumberManager);
        addDataManager(infoManager);
        addDataManager(recordManager);

        // 指定标题栏的布局适配器，对应的实体是String
        headerAdapter = new HeaderAdapter();
        registerBinder(headerAdapter);
        // 指定9宫格的布局适配器，对应的实体是StarNumberItem
        starNumAdapter = new StarNumAdapter();
        registerBinder(starNumAdapter);
        // 指定info的布局适配器，对应的实体是StudioPageItem
        infoAdapter = new InfoAdapter();
        registerBinder(infoAdapter);
        // 指定record列表布局适配器，对应的实体是RecordProxy
        recordAdapter = new RecordAdapter();
        registerBinder(recordAdapter);
    }

    public void setOnSeeAllListener(HeaderAdapter.OnSeeAllListener onSeeAllListener) {
        headerAdapter.setOnSeeAllListener(onSeeAllListener);
    }

    public void setOnClickStarListener(StarNumAdapter.OnClickStarListener onClickStarListener) {
        starNumAdapter.setOnClickStarListener(onClickStarListener);
    }

    public void setOnClickRecordListener(RecordAdapter.OnClickRecordListener onClickRecordListener) {
        recordAdapter.setOnClickRecordListener(onClickRecordListener);
    }

    public void setPageItem(StudioPageItem pageItem) {
        this.pageItem = pageItem;
        starNumberManager.clear();
        recordManager.clear();
        if (pageItem != null) {
            createAdapter();
        }
    }

    private void createAdapter() {
        if (ListUtil.isEmpty(pageItem.getStarList())) {
            headManager.add("Top Stars");
        }
        else {
            headManager.add("Top " + pageItem.getStarList().size() + " Stars");
        }
        starNumberManager.addAll(pageItem.getStarList());
        infoManager.add(pageItem);
        recordManager.addAll(pageItem.getRecordList());
    }

    private int getRecordStarPosition() {
        // star title + star grid + info
        return 1 + pageItem.getStarList().size() + 1;
    }

    public RecyclerView.ItemDecoration getItemDecorator() {
        return new RecordDecoration();
    }

    private class RecordDecoration extends RecyclerView.ItemDecoration {

        private Paint mPaint;

        private int dividerHeight = ScreenUtils.dp2px(10);

        public RecordDecoration() {
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.parseColor("#efefef"));
            mPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            // 从record开始才添加分隔
            if (position > getRecordStarPosition()) {
                outRect.top = dividerHeight;
            }
            else {
                outRect.top = 0;
            }
        }

        @Override
        public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(canvas, parent, state);
            int left = parent.getPaddingLeft();
            int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            int childSize = parent.getChildCount();
            if (childSize > 0) {
                // 从record开始才添加分隔
                int startPosition = getRecordStarPosition() + 1;
                for (int i = startPosition; i < childSize; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
                    int top = child.getBottom() + layoutParams.bottomMargin;
                    int bottom = top + dividerHeight;
                    canvas.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }
}
