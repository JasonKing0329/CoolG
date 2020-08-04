package com.king.app.coolg.phone.image;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.BitmapFactory;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.bean.StarDetailBuilder;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.FileUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.Star;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/8/4 9:40
 */
public class ImageViewModel extends BaseViewModel {

    public MutableLiveData<List<ImageBean>> imageList = new MutableLiveData<>();
    private int mStaggerImageWidth;

    public ImageViewModel(Application application) {
        super(application);
        int margin = ScreenUtils.dp2px(1);
        int column = 2;
        mStaggerImageWidth = ScreenUtils.getScreenWidth() / column - margin;
    }

    public void loadStarImages(long starId) {
        Star star = getDaoSession().getStarDao().load(starId);
        List<String> list = ImageProvider.getStarPathList(star.getName());
        convertToImages(list);
    }

    public void loadRecordImages(long recordId) {
        Record record = getDaoSession().getRecordDao().load(recordId);
        List<String> list = ImageProvider.getRecordPathList(record.getName());
        convertToImages(list);
    }

    private void convertToImages(List<String> list) {
        toImageBean(list, true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<ImageBean> list) {
                        imageList.setValue(list);
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

    private Observable<List<ImageBean>> toImageBean(List<String> list, boolean loadSize) {
        return Observable.create(e -> {
            List<ImageBean> result = new ArrayList<>();
            for (String path:list) {
                ImageBean bean = new ImageBean();
                bean.setUrl(path);
                if (loadSize) {
                    calcImageSize(bean);
                }
                result.add(bean);
            }
            e.onNext(result);
            e.onComplete();
        });
    }

    private void calcImageSize(ImageBean bean) {
        // 无图按16:9
        //缩放图片的实际宽高
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bean.getUrl(), options);
        int height = options.outHeight;
        int width = options.outWidth;
        float ratio = (float) mStaggerImageWidth / (float) width;
        bean.setWidth(mStaggerImageWidth);
        height = (int) (height * ratio);
        bean.setHeight(height);
    }

    public void onSelectAll(boolean select) {
        if (imageList.getValue() != null) {
            for (ImageBean bean:imageList.getValue()) {
                bean.setSelected(select);
            }
        }
    }

    public void deleteSelectedItems() {
        if (imageList.getValue() != null) {
            List<ImageBean> delList = new ArrayList<>();
            for (ImageBean bean : imageList.getValue()) {
                if (bean.isSelected()) {
                    FileUtil.deleteFile(new File(bean.getUrl()));
                    delList.add(bean);
                }
            }
            for (ImageBean delBean:delList) {
                imageList.getValue().remove(delBean);
            }
        }
    }
}
