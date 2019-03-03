package com.king.app.coolg.pad.record;

import android.arch.lifecycle.Lifecycle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.king.app.coolg.GlideApp;
import com.king.app.coolg.R;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.lib.banner.CoolBannerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/8/25 0025.
 */

public class RecordPagerAdapter extends CoolBannerAdapter<String> {

    private Lifecycle lifecycle;
    private List<View> viewList;

    private OnHolderListener onHolderListener;
    public RecordPagerAdapter(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }

    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.adapter_record_banner_item_pad;
    }

    @Override
    protected void onBindView(View view, int position, String bean) {
        ImageView imageView = view.findViewById(R.id.iv_image);
        GlideApp.with(imageView.getContext())
                .asBitmap()
                .load(bean)
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
                .into(imageView);
    }

    public interface OnHolderListener {
        void onPaletteCreated(int position, Palette palette);
        void onBoundsCreated(int position, List<ViewColorBound> bounds);
    }
}
