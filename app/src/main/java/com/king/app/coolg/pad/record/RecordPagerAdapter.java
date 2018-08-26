package com.king.app.coolg.pad.record;

import android.arch.lifecycle.Lifecycle;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.app.coolg.view.widget.banner.BannerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/8/25 0025.
 */

public class RecordPagerAdapter extends BannerAdapter {

    private Lifecycle lifecycle;
    private List<View> viewList;

    private OnHolderListener onHolderListener;

    private List<String> list;

    public RecordPagerAdapter(ViewPager viewPager, Lifecycle lifecycle, List<View> viewList) {
        super(viewPager);
        this.lifecycle = lifecycle;
        this.viewList = viewList;
    }

    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0:list.size();
    }

    @Override
    protected void onBindItem(int position, View imageView) {
        GlideApp.with(imageView.getContext())
                .asBitmap()
                .load(list.get(position))
                // listener只能添加一个，所以用RecordBitmapListener包含BitmapPaletteListener and TargetViewListener的处理
                .listener(new RecordBitmapListener(viewList, lifecycle) {
                    @Override
                    protected void onPaletteCreated(Palette palette) {
                        if (onHolderListener != null) {
                            onHolderListener.onPaletteCreated(position, palette);
                        }
                    }

                    @Override
                    protected void onBoundsCreated(List<ViewColorBound> bounds) {
                        if (onHolderListener != null) {
                            onHolderListener.onBoundsCreated(position, bounds);
                        }
                    }
                })
                .into((ImageView) imageView);
    }

    @Override
    protected View onCreateView() {
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setClickable(true);
        imageView.setOnClickListener(this);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    public interface OnHolderListener {
        void onPaletteCreated(int position, Palette palette);
        void onBoundsCreated(int position, List<ViewColorBound> bounds);
    }
}
