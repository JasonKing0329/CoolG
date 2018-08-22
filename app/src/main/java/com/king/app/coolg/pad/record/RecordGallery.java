package com.king.app.coolg.pad.record;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BindingDialogFragment;
import com.king.app.coolg.base.adapter.BaseRecyclerAdapter;
import com.king.app.coolg.databinding.DialogRecordGalleryBinding;
import com.king.app.coolg.utils.ScreenUtils;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 13:38
 */
public class RecordGallery extends BindingDialogFragment<DialogRecordGalleryBinding> {

    private List<String> list;

    private int currentPage;

    private RecordGalleryAdapter adapter;

    private BaseRecyclerAdapter.OnItemClickListener<String> onItemClickListener;

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_record_gallery;
    }

    @Override
    protected void initView(View view) {

    }

    @Override
    public void onResume() {
        super.onResume();

        // 在initView里不起作用
        setWidth(ScreenUtils.getScreenWidth());

        mBinding.rvGallery.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mBinding.rvGallery.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                if (position > 0) {
                    outRect.left = ScreenUtils.dp2px(15);
                }
            }
        });
        
        if (adapter == null) {
            adapter = new RecordGalleryAdapter();
            adapter.setList(list);
            mBinding.rvGallery.setAdapter(adapter);
        }
        else {
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
        adapter.setOnItemClickListener(onItemClickListener);
        adapter.updateSelection(currentPage);
        mBinding.rvGallery.scrollToPosition(currentPage);
    }

    public void setImageList(List<String> list) {
        this.list = list;
    }

    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener<String> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        if (isVisible()) {
            adapter.updateSelection(currentPage);
            mBinding.rvGallery.scrollToPosition(currentPage);
        }
    }
}
