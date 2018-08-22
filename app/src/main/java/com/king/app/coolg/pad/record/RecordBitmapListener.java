package com.king.app.coolg.pad.record;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.king.app.coolg.model.palette.BitmapRepository;
import com.king.app.coolg.model.palette.ViewColorBound;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc: integration of BitmapPaletteListener and TargetViewListener
 * Glide.xxx.listener().listener()只有最后一个会收到回调，因此用这个listener来处理两个listener处理的内容
 *
 * @author：Jing Yang
 * @date: 2018/8/2 15:53
 */
public abstract class RecordBitmapListener implements RequestListener<Bitmap>, LifecycleObserver {

    private List<View> viewList;
    private Disposable disposable;

    public RecordBitmapListener(List<View> list, Lifecycle lifecycle) {
        this.viewList = list;
        lifecycle.addObserver(this);
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        BitmapRepository repository = new BitmapRepository();

        repository.createViewColorBound(viewList, resource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ViewColorBound>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<ViewColorBound> bounds) {
                        onBoundsCreated(bounds);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

        repository.createPalette(resource)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Palette>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Palette palette) {
                        onPaletteCreated(palette);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return false;
    }

    protected abstract void onPaletteCreated(Palette palette);

    protected abstract void onBoundsCreated(List<ViewColorBound> bounds);

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onLifeDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
