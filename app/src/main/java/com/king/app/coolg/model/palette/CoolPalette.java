package com.king.app.coolg.model.palette;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/2 15:53
 */
public abstract class CoolPalette implements RequestListener<Bitmap>, LifecycleObserver {

    private Disposable disposable;

    public CoolPalette(Lifecycle lifecycle) {
        lifecycle.addObserver(this);
    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        createPalette(resource)
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

    private Observable<Palette> createPalette(final Bitmap resource) {
        return Observable.create(e -> Palette.from(resource)
                .generate(palette -> e.onNext(palette)));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onLifeDestroy() {
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
