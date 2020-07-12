package com.king.app.coolg.pad.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.king.app.coolg.R;
import com.king.app.coolg.phone.star.StarRelationship;
import com.king.app.coolg.phone.star.StarViewModel;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.StarRating;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/20 16:30
 */
public class StarPadViewModel extends StarViewModel {

    public MutableLiveData<List<StarImageBean>> imagesObserver = new MutableLiveData<>();
    public MutableLiveData<List<StarRelationship>> relationshipsObserver = new MutableLiveData<>();
    public MutableLiveData<StarRating> ratingObserver = new MutableLiveData<>();

    private int singleColImgWidth;
    private int twoColImgWidth;
    private int imgMarginSingle;
    private int starImgSpan;

    public StarPadViewModel(@NonNull Application application) {
        super(application);
        singleColImgWidth = application.getResources().getDimensionPixelOffset(R.dimen.star_page_image_width_pad);
        imgMarginSingle = ScreenUtils.dp2px(1);
        twoColImgWidth = singleColImgWidth / 2 - imgMarginSingle;
    }

    public int getImgMarginSingle() {
        return imgMarginSingle;
    }

    @Override
    public void loadStar(long starId) {
        loadingObserver.setValue(true);
        starRepository.getStar(starId)
                .flatMap(star -> {
                    mStar = star;
                    starObserver.postValue(mStar);
                    loadRating();
                    return getStarImages(star);
                })
                .flatMap(list -> {
                    starImageList = list;
                    return toStarImageBeans(list);
                })
                .flatMap(list -> {
                    imagesObserver.postValue(list);
                    return getStarTags(mStar);
                })
                .flatMap(list -> {
                    tagList = list;
                    tagsObserver.postValue(list);
                    return getRelationships(mStar);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarRelationship>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarRelationship> list) {
                        loadingObserver.setValue(false);
                        relationshipsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        loadingObserver.setValue(false);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public int getStarImgSpan() {
        return starImgSpan;
    }

    private ObservableSource<List<StarImageBean>> toStarImageBeans(List<String> list) {
        return observer -> {
            starImgSpan = list.size() < 3 ? 1:2;
            List<StarImageBean> result = new ArrayList<>();
            for (String path:list) {
                StarImageBean bean = new StarImageBean();
                bean.setPath(path);
                calcImageSize(bean);
                result.add(bean);
            }
            observer.onNext(result);
            observer.onComplete();
        };
    }

    private void calcImageSize(StarImageBean bean) {
        //获取图片的宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bean.getPath(), options);
        int height = options.outHeight;
        int width = options.outWidth;
        float ratio;
        if (starImgSpan == 1) {
            ratio = (float) singleColImgWidth / (float) width;
            bean.setWidth(singleColImgWidth);
        }
        else {
            ratio = (float) twoColImgWidth / (float) width;
            bean.setWidth(twoColImgWidth);
        }
        height = (int) (height * ratio);
        bean.setHeight(height);
    }

    public void loadRating() {
        if (mStar.getRatings().size() > 0) {
            ratingObserver.postValue(mStar.getRatings().get(0));
        }
        else {
            ratingObserver.postValue(null);
        }
    }

    public void reloadImages() {
        getStarImages(mStar)
                .flatMap(list -> {
                    starImageList = list;
                    return toStarImageBeans(list);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarImageBean> starImageBeans) {
                        imagesObserver.setValue(starImageBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
