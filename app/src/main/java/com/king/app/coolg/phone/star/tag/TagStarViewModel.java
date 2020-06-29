package com.king.app.coolg.phone.star.tag;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.repository.TagRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.record.list.RecordProxy;
import com.king.app.coolg.phone.star.list.StarListViewModel;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.gdb.data.RecordCursor;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.entity.TagStar;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.param.DataConstants;

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

    public TagStarViewModel(@NonNull Application application) {
        super(application);
        mTagSortType = SettingProperty.getTagSortType();
        tagRepository = new TagRepository();
        starRepository = new StarRepository();
        mSortType = AppConstants.SCENE_SORT_NAME;
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
        starRepository.queryTagStars(tagId)
                .flatMap(list -> starRepository.sortStars(list, mSortType))
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
        starRepository.sortStars(mStarList, mSortType)
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

}
