package com.king.app.coolg.pad.record;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.king.app.coolg.GlideApp;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.app.coolg.utils.DebugLog;

import java.util.List;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/22 14:56
 */
public class RecordImageHolder implements Holder<String> {

    private ImageView imageView;
    private Lifecycle lifecycle;
    private List<View> viewList;

    private OnHolderListener onHolderListener;

    public RecordImageHolder(Lifecycle lifecycle, List<View> viewList) {
        this.lifecycle = lifecycle;
        this.viewList = viewList;
    }

    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }

    @Override
    public View createView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        GlideApp.with(context)
                .asBitmap()
                .load(data)
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
