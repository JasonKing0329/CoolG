package com.king.app.coolg.pad.gallery;

import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/7/11 23:18
 */
public class GalleryAdapter extends PagerAdapter {

    private List<ThumbBean> list;

    public void setList(List<ThumbBean> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list == null ? 0:list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view == o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ConstraintLayout parent = new ConstraintLayout(container.getContext());
        ImageView iv = new ImageView(container.getContext());
        GlideApp.with(iv.getContext())
                .load(list.get(position).getImagePath())
                .into(iv);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(list.get(position).getWidth(), list.get(position).getHeight());
        params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        parent.addView(iv, params);
        container.addView(parent, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return parent;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
