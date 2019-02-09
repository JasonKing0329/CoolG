package com.king.app.coolg.phone.star.category;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;
import com.king.app.coolg.base.adapter.TwoTypeBindingAdapter;
import com.king.app.coolg.databinding.AdapterCategoryLevelBinding;
import com.king.app.coolg.utils.ScreenUtils;

public class LevelAdapter extends BaseBindingAdapter<AdapterCategoryLevelBinding, CategoryLevel> {

    private OnLevelListener onLevelListener;

    public void setOnLevelListener(OnLevelListener onLevelListener) {
        this.onLevelListener = onLevelListener;
    }

    @Override
    protected int getItemLayoutRes() {
        return R.layout.adapter_category_level;
    }

    @Override
    protected void onBindItem(AdapterCategoryLevelBinding binding, int position, CategoryLevel bean) {
        binding.setBean(bean);
        if (binding.rvStars.getAdapter() == null) {
            binding.rvStars.setLayoutManager(new LinearLayoutManager(binding.rvStars.getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvStars.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.left = ScreenUtils.dp2px(8);
                }
            });
            CandidateAdapter adapter = new CandidateAdapter();
            adapter.setList(bean.getStarList());
            adapter.setOnItemClickListener(new TwoTypeBindingAdapter.OnItemClickListener<CategoryStar, CategoryAdd>() {
                @Override
                public void onClickType1(View view, int position, CategoryStar data) {
                    onLevelListener.onSelectLevelStar(bean, data);
                }

                @Override
                public void onClickType2(View view, int position, CategoryAdd data) {

                }
            });
            binding.rvStars.setAdapter(adapter);
        }
        else {
            CandidateAdapter adapter = (CandidateAdapter) binding.rvStars.getAdapter();
            adapter.setList(bean.getStarList());
            adapter.notifyDataSetChanged();
        }

        binding.ivAdd.setOnClickListener(v -> onLevelListener.onInsertLevel(bean));

        binding.ivDelete.setOnClickListener(v -> onLevelListener.onRemoveLevel(bean));
    }

    @Override
    protected void onClickItem(View v, int position) {
        // 单选情况下要撤销上次选中
        for (int i = 0; i < getItemCount(); i ++) {
            list.get(i).setSelected(position == i);
        }
    }

    public void notifyLevelChanged(CategoryLevel level) {
        for (int i = 0; i < getItemCount(); i ++) {
            if (level == list.get(i)) {
                notifyItemChanged(i);
                break;
            }
        }
    }

    public interface OnLevelListener {
        void onInsertLevel(CategoryLevel level);

        void onSelectLevelStar(CategoryLevel bean, CategoryStar data);

        void onRemoveLevel(CategoryLevel bean);
    }
}
