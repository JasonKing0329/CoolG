package com.king.app.coolg.pad.gallery;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableInt;
import android.graphics.BitmapFactory;
import android.view.View;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @description:
 * @author：Jing
 * @date: 2020/7/11 23:25
 */
public class GalleryViewModel extends BaseViewModel {

    public MutableLiveData<List<ThumbBean>> itemsObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> setPosition = new MutableLiveData<>();

    public ObservableInt previousVisibility = new ObservableInt(View.GONE);
    public ObservableInt nextVisibility = new ObservableInt(View.GONE);

    private int thumbHeight;
    private int selection;

    public GalleryViewModel(Application application) {
        super(application);
        thumbHeight = application.getResources().getDimensionPixelSize(R.dimen.image_pager_thumb_height);
    }

    public void convertImages(List<String> list) {
        doConvert(list)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ThumbBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<ThumbBean> thumbBeans) {
                        itemsObserver.setValue(thumbBeans);
                        if (selection != 0) {
                            setPosition.setValue(selection);
                        }
                        onSelectionChanged(selection);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<ThumbBean>> doConvert(List<String> list) {
        return Observable.create(e -> {
            List<ThumbBean> result = new ArrayList<>();
            for (String path:list) {
                ThumbBean bean = new ThumbBean();
                bean.setImagePath(path);
                setImageSize(bean);
                result.add(bean);
            }
            e.onNext(result);
            e.onComplete();
        });
    }

    private void setImageSize(ThumbBean bean) {
        //获取图片的宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bean.getImagePath(), options);
        int height = options.outHeight;
        int width = options.outWidth;
        setBigSize(bean, width, height);
        setThumbSize(bean, width, height);
    }

    private void setThumbSize(ThumbBean bean, int width, int height) {
        float ratio = (float) thumbHeight / (float) height;
        bean.setThumbHeight(thumbHeight);
        bean.setThumbWidth((int) (width * ratio));
    }

    private void setBigSize(ThumbBean bean, int width, int height) {
        int screenWidth = ScreenUtils.getScreenWidth();
        int screenHeight = ScreenUtils.getScreenHeight();
        float ratioImg = (float) width / (float) height;
        float ratioScreen = (float) screenWidth / (float) screenHeight;
        // 以宽为基准
        if (ratioImg > ratioScreen) {
            // 大于屏幕宽，缩小
            if (width >= screenWidth) {
                float ratio = (float) screenWidth / (float) width;
                bean.setWidth(screenWidth);
                bean.setHeight((int) (height * ratio));
            }
            else {
                // 大于screenWidth的2/3小于全部，直接放大到全部
                int toPerThree = screenWidth * 2 / 3;
                if (width > toPerThree) {
                    float ratio = (float) screenWidth / (float) width;
                    bean.setWidth(screenWidth);
                    bean.setHeight((int) (height * ratio));
                }
                // 其他情况扩大三分之一显示
                else {
                    float ratio = 1 + 1f/3f;
                    bean.setWidth((int) (width * ratio));
                    bean.setHeight((int) (height * ratio));
                }
            }
        }
        // 以高为基准
        else {
            // 大于屏幕宽，缩小
            if (height >= screenHeight) {
                float ratio = (float) screenHeight / (float) height;
                bean.setHeight(screenHeight);
                bean.setWidth((int) (width * ratio));
            }
            else {
                // 大于screenWidth的2/3小于全部，直接放大到全部
                int toPerThree = screenHeight * 2 / 3;
                if (height > toPerThree) {
                    float ratio = (float) screenHeight / (float) height;
                    bean.setHeight(screenHeight);
                    bean.setWidth((int) (width * ratio));
                }
                // 其他情况不进行缩放
                else {
                    // 其他情况扩大三分之一显示
                    float ratio = 1 + 1f/3f;
                    bean.setWidth((int) (width * ratio));
                    bean.setHeight((int) (height * ratio));
                }
            }
        }
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    private int getItemSize() {
        return itemsObserver.getValue() == null ? 0:itemsObserver.getValue().size();
    }

    public void onSelectionChanged(int position) {
        selection = position;
        if (selection != 0) {
            previousVisibility.set(selection > 0 ? View.VISIBLE:View.GONE);
            nextVisibility.set(selection < getItemSize() - 1 ? View.VISIBLE: View.GONE);
        }
        else {
            previousVisibility.set(View.GONE);
            nextVisibility.set(getItemSize() > 0 ? View.VISIBLE:View.GONE);
        }
    }

    public void onNext() {
        selection ++;
        setPosition.setValue(selection);
        onSelectionChanged(selection);
    }

    public void onPrevious() {
        selection --;
        setPosition.setValue(selection);
        onSelectionChanged(selection);
    }
}
