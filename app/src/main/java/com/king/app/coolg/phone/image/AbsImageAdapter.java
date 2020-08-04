package com.king.app.coolg.phone.image;

import android.databinding.ViewDataBinding;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.base.adapter.BaseBindingAdapter;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/4 10:28
 */
public abstract class AbsImageAdapter<V extends ViewDataBinding> extends BaseBindingAdapter<V, ImageBean> {

    private boolean isSelectMode;

    public void setSelectMode(boolean selectMode) {
        isSelectMode = selectMode;
        notifyDataSetChanged();
    }

    protected void setCheckVisibility(CheckBox checkBox) {
        checkBox.setVisibility(isSelectMode ? View.VISIBLE:View.GONE);
    }

    protected void setImage(ImageView imageView, ImageBean bean) {
        GlideApp.with(imageView.getContext())
                .load(bean.getUrl())
                .error(R.drawable.def_small)
                .into(imageView);
    }

    @Override
    protected void onClickItem(View v, int position) {
        if (isSelectMode) {
            list.get(position).setSelected(!list.get(position).isSelected());
        }
        else {
            super.onClickItem(v, position);
        }
    }
}
