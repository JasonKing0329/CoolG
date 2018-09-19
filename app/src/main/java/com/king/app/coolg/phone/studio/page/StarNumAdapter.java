package com.king.app.coolg.phone.studio.page;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.ItemDataBinder;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.databinding.AdapterStudioPageStarBinding;

/**
 * Desc: 混合布局里的9宫格布局，关联实体StarNumberItem
 *
 * @author：Jing Yang
 * @date: 2018/9/18 17:05
 */
public class StarNumAdapter extends ItemDataBinder<StarNumberItem, AdapterStudioPageStarBinding> {

    private OnClickStarListener onClickStarListener;

    public void setOnClickStarListener(OnClickStarListener onClickStarListener) {
        this.onClickStarListener = onClickStarListener;
    }

    @Override
    protected void bindModel(StarNumberItem item, AdapterStudioPageStarBinding binding) {
        GlideApp.with(binding.ivImage.getContext())
                .load(item.getImageUrl())
                .error(R.drawable.def_person_square)
                .into(binding.ivImage);
        binding.tvNum.setText(String.valueOf(item.getNumber()));
        binding.getRoot().setOnClickListener(v -> {
            if (onClickStarListener != null) {
                onClickStarListener.onClickStar(item);
            }
        });
    }

    @Override
    protected AdapterStudioPageStarBinding createBinding(LayoutInflater inflater, ViewGroup parent) {
        return AdapterStudioPageStarBinding.inflate(inflater);
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof StarNumberItem;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        // super.getSpanSize(maxSpanCount)返回就是1
        return super.getSpanSize(maxSpanCount);
    }

    public interface OnClickStarListener {
        void onClickStar(StarNumberItem item);
    }
}
