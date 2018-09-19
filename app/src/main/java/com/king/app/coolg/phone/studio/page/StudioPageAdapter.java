package com.king.app.coolg.phone.studio.page;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ahamed.multiviewadapter.DataItemManager;
import com.ahamed.multiviewadapter.DataListManager;
import com.ahamed.multiviewadapter.RecyclerAdapter;
import com.king.app.coolg.phone.record.list.RecordProxy;
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
    private DataListManager<StarNumberItem> starNumberManager;
    private DataListManager<RecordProxy> recordManager;

    private HeaderAdapter headerAdapter;
    private StarNumAdapter starNumAdapter;
    private RecordAdapter recordAdapter;

    private StudioPageItem pageItem;

    public StudioPageAdapter() {
        starNumberManager = new DataListManager<>(this);
        recordManager = new DataListManager<>(this);

        // 指定混合模式的布局顺序
        // 这里是 标题栏-9宫格star-标题栏-record列表，标题栏直接使用了String
        addDataManager(new DataItemManager(this, "Top 9 stars"));
        addDataManager(starNumberManager);
        addDataManager(new DataItemManager(this, "Records"));
        addDataManager(recordManager);

        // 指定标题栏的布局适配器，对应的实体是String
        headerAdapter = new HeaderAdapter();
        registerBinder(headerAdapter);
        // 指定9宫格的布局适配器，对应的实体是StarNumberItem
        starNumAdapter = new StarNumAdapter();
        registerBinder(starNumAdapter);
        // 指定record列表布局适配器，对应的实体是RecordProxy
        recordAdapter = new RecordAdapter();
        registerBinder(recordAdapter);
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
        starNumberManager.addAll(pageItem.getStarList());
        recordManager.addAll(pageItem.getRecordList());
    }

    private int getRecordStarPosition() {
        return 1 + pageItem.getStarList().size() + 1;
    }

    public RecyclerView.ItemDecoration getItemDecorator() {
        return new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > getRecordStarPosition()) {
                    outRect.top = ScreenUtils.dp2px(10);
                }
                else {
                    outRect.top = 0;
                }
            }
        };
    }
}
