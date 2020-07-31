package com.king.app.coolg.phone.star.tag;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.base.CoolApplication;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.repository.TagRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/6/29 13:19
 */
public class TagStarViewModel extends BaseViewModel {

    public MutableLiveData<List<Tag>> tagsObserver = new MutableLiveData<>();
    public MutableLiveData<List<StarProxy>> starsObserver = new MutableLiveData<>();
    public MutableLiveData<Integer> focusTagPosition = new MutableLiveData<>();

    private List<StarProxy> mStarList;

    private Long mTagId;

    private int mTagSortType;

    private List<Tag> dataTagList;

    private TagRepository tagRepository;
    private StarRepository starRepository;

    // see AppConstants.STAR_SORT_XXX
    private int mSortType;

    /**
     * 瀑布流模式下item宽度
     */
    private int mStaggerColWidth;

    private int viewColumn;

    // see AppConstants.TAG_STAR_XXX
    private int viewType;

    public TagStarViewModel(@NonNull Application application) {
        super(application);
        mTagSortType = SettingProperty.getTagSortType();
        tagRepository = new TagRepository();
        starRepository = new StarRepository();
        mSortType = AppConstants.STAR_SORT_RATING;

    }

    public void setListViewType(int type, int column) {
        viewType = type;
        viewColumn = column;
        if (viewType == AppConstants.TAG_STAR_STAGGER) {
            int margin = ScreenUtils.dp2px(1);
            mStaggerColWidth = ScreenUtils.getScreenWidth() / column - margin;
            if (ScreenUtils.isTablet()) {
                int extra = CoolApplication.getInstance().getResources().getDimensionPixelSize(R.dimen.tag_star_pad_tag_width);
                mStaggerColWidth = (ScreenUtils.getScreenWidth() - extra) / column - margin;
            }
        }
    }

    public int getViewType() {
        return viewType;
    }

    public int getViewColumn() {
        return viewColumn;
    }

    public void loadTags() {
        dataTagList = tagRepository.loadTags(DataConstants.TAG_TYPE_STAR);
        startSortTag(true);
    }

    public void startSortTag(boolean loadAll) {
        tagRepository.sortTags(mTagSortType, dataTagList)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Tag>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Tag> tagList) {
                        List<Tag> allList = addTagAll(tagList);
                        tagsObserver.setValue(allList);

                        if (loadAll) {
                            loadTagStars(allList.get(0).getId());
                        }
                        else {
                            focusToCurrentTag(allList);
                        }
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

    private void focusToCurrentTag(List<Tag> allList) {
        for (int i = 0; i < allList.size(); i ++) {
            if (mTagId == allList.get(i).getId()) {
                focusTagPosition.setValue(i);
                break;
            }
        }
    }

    private List<Tag> addTagAll(List<Tag> tagList) {
        List<Tag> tags = new ArrayList<>();
        Tag all = new Tag();
        all.setName("All");
        tags.add(all);
        if (!ListUtil.isEmpty(tagList)) {
            tags.addAll(tagList);
        }
        return tags;
    }

    public void loadTagStars() {
        loadTagStars(mTagId);
    }

    public void loadTagStars(Long tagId) {
        mTagId = tagId;
        Observable<List<StarProxy>> observable = starRepository.queryTagStars(tagId)
                .flatMap(list -> starRepository.sortStars(list, mSortType));
        if (viewType == AppConstants.TAG_STAR_STAGGER) {
            observable = observable.flatMap(list -> toStaggerItems(list));
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarProxy> list) {
                        mStarList = list;
                        starsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void sortList(int sortType) {
        if (sortType == mSortType) {
            return;
        }
        mSortType = sortType;
        Observable<List<StarProxy>> observable = starRepository.sortStars(mStarList, mSortType);
        if (viewType == AppConstants.TAG_STAR_STAGGER) {
            observable = observable.flatMap(list -> toStaggerItems(list));
        }
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<StarProxy>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<StarProxy> list) {
                        mStarList = list;
                        starsObserver.setValue(list);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        messageObserver.setValue("Load records error: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void onTagSortChanged() {
        mTagSortType = SettingProperty.getTagSortType();
    }

    private ObservableSource<List<StarProxy>> toStaggerItems(List<StarProxy> list) {
        return observer -> {
            for (StarProxy proxy:list) {
                calcImageSize(proxy);
            }
            observer.onNext(list);
            observer.onComplete();
        };
    }

    private void calcImageSize(StarProxy bean) {
        // 无图按16:9
        if (bean.getImagePath() == null) {
            bean.setWidth(mStaggerColWidth);
            bean.setHeight(mStaggerColWidth * 9 / 16);
        }
        else {
            //缩放图片的实际宽高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bean.getImagePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;
            float ratio = (float) mStaggerColWidth / (float) width;
            bean.setWidth(mStaggerColWidth);
            height = (int) (height * ratio);
            bean.setHeight(height);
        }
    }

}
