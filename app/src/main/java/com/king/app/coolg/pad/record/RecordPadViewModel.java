package com.king.app.coolg.pad.record;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;

import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.VideoModel;
import com.king.app.coolg.model.palette.ViewColorBound;
import com.king.app.coolg.model.repository.RecordRepository;
import com.king.app.coolg.phone.record.PassionPoint;
import com.king.app.coolg.phone.record.RecordViewModel;
import com.king.app.coolg.phone.video.player.PlayListInstance;
import com.king.app.gdb.data.entity.PlayItem;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;
import com.king.app.gdb.data.entity.Tag;
import com.king.app.gdb.data.param.DataConstants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * @date: 2018/8/22 13:46
 */
public class RecordPadViewModel extends RecordViewModel {

    private List<String> mImageList;

    private RecordRepository repository;

    public MutableLiveData<Record> recordObserver = new MutableLiveData<>();
    public MutableLiveData<String> videoPathObserver = new MutableLiveData<>();
    public MutableLiveData<List<String>> imagesObserver = new MutableLiveData<>();
    public MutableLiveData<List<RecordStar>> starsObserver = new MutableLiveData<>();
    public MutableLiveData<List<TitleValueBean>> scoreObserver = new MutableLiveData<>();
    public MutableLiveData<List<PassionPoint>> passionsObserver = new MutableLiveData<>();
    public MutableLiveData<Palette> paletteObserver = new MutableLiveData<>();
    public MutableLiveData<List<ViewColorBound>> viewBoundsObserver = new MutableLiveData<>();
    public MutableLiveData<Boolean> videoPlayOnReadyObserver = new MutableLiveData<>();

    private Map<Integer, Palette> paletteMap;
    private Map<Integer, List<ViewColorBound>> viewBoundsMap;

    public RecordPadViewModel(@NonNull Application application) {
        super(application);
        repository = new RecordRepository();
        paletteMap = new HashMap<>();
        viewBoundsMap = new HashMap<>();
    }

    @Override
    public void loadRecord(long recordId) {
        repository.getRecord(recordId)
                .flatMap(record -> {
                    mRecord = record;
                    recordObserver.postValue(record);
                    starsObserver.postValue(record.getRelationList());
                    videoPathObserver.postValue(VideoModel.getVideoPath(record.getName()));
                    return loadScores();
                })
                .flatMap(list -> {
                    scoreObserver.postValue(list);
                    return loadPassionPoints();
                })
                .flatMap(list -> {
                    passionsObserver.postValue(list);
                    return loadTags();
                })
                .flatMap(list -> {
                    tagsObserver.postValue(list);
                    return loadImages();
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        imagesObserver.setValue(strings);

                        checkPlayable();
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

    private ObservableSource<List<PassionPoint>> loadPassionPoints() {
        return observer -> observer.onNext(getPassions(mRecord));
    }

    private ObservableSource<List<TitleValueBean>> loadScores() {
        return observer -> observer.onNext(getScoreDetails());
    }

    private Observable<List<String>> loadImages() {
        return Observable.create(observer -> {
            if (ImageProvider.hasRecordFolder(mRecord.getName())) {
                mImageList = ImageProvider.getRecordPathList(mRecord.getName());
            }
            else {
                mImageList = new ArrayList<>();
                String path = ImageProvider.getRecordRandomPath(mRecord.getName(), null);
                mImageList.add(path);
            }
//            mImageList = ImageProvider.getRecordPathList("123");
            Collections.shuffle(mImageList);
            observer.onNext(mImageList);
        });
    }

    private ObservableSource<List<Tag>> loadTags() {
        return observer -> observer.onNext(getTags(mRecord));
    }

    @Override
    public void deleteImage(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            loadImages(mRecord);
        }
    }

    @Override
    protected void loadImages(Record record) {
        loadImages()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<String> strings) {
                        imagesObserver.setValue(strings);
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

    public List<String> getImageList() {
        return mImageList;
    }

    public List<TitleValueBean> getScoreDetails() {
        List<TitleValueBean> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        newValue(list, sdf.format(new Date(mRecord.getLastModifyTime())));
        newTitleValue(list, "HD level", mRecord.getHdLevel());
        newTitleValue(list, "Feel", mRecord.getScoreFeel());
        newTitleValue(list, "Stars", mRecord.getScoreStar());
        newTitleValue(list, "Passion", mRecord.getScorePassion());
        newTitleValue(list, "Cum", mRecord.getScoreCum());
        newTitleValue(list, "Special", mRecord.getScoreSpecial());
        newValue(list, mRecord.getSpecialDesc());
        if (mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_1V1) {
            RecordType1v1 record = mRecord.getRecordType1v1();
            newTitleValue(list, "BJob", record.getScoreBjob());
            newTitleValue(list, "Scene", record.getScoreScene());
            newTitleValue(list, "CShow", record.getScoreCshow());
            newTitleValue(list, "Rhythm", record.getScoreRhythm());
            newTitleValue(list, "Story", record.getScoreStory());
            newTitleValue(list, "Rim", record.getScoreRim());
            newTitleValue(list, "Foreplay", record.getScoreForePlay());
        } else if (mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_3W
                || mRecord.getType() == DataConstants.VALUE_RECORD_TYPE_MULTI) {
            RecordType3w record = mRecord.getRecordType3w();
            newTitleValue(list, "BJob", record.getScoreBjob());
            newTitleValue(list, "Scene", record.getScoreScene());
            newTitleValue(list, "CShow", record.getScoreCshow());
            newTitleValue(list, "Rhythm", record.getScoreRhythm());
            newTitleValue(list, "Story", record.getScoreStory());
            newTitleValue(list, "Rim", record.getScoreRim());
            newTitleValue(list, "Foreplay", record.getScoreForePlay());
        }
        return list;
    }

    private void newValue(List<TitleValueBean> list, String value) {
        if (!TextUtils.isEmpty(value)) {
            TitleValueBean bean = new TitleValueBean();
            bean.setValue(value);
            list.add(bean);
        }
    }

    private void newTitleValue(List<TitleValueBean> list, String title, int value) {
        if (value > 0) {
            TitleValueBean bean = new TitleValueBean();
            bean.setTitle(title);
            bean.setValue(String.valueOf(value));
            list.add(bean);
        }
    }

    public void refreshBackground(int position) {
        paletteObserver.setValue(paletteMap.get(position));
        viewBoundsObserver.setValue(viewBoundsMap.get(position));
    }

    public void cachePalette(int position, Palette palette) {
        paletteMap.put(position, palette);
    }

    public void cacheViewBounds(int position, List<ViewColorBound> bounds) {
        viewBoundsMap.put(position, bounds);
    }

    public String getCurrentImage(int currentPage) {
        try {
            return mImageList.get(currentPage);
        } catch (Exception e) {
            return null;
        }
    }

    public void playVideo() {
        // 将视频url添加到播放列表的末尾
        PlayListInstance.getInstance().addRecord(mRecord, mPlayUrl);
        PlayListInstance.getInstance().setPlayIndexAsLast();
        videoPlayOnReadyObserver.setValue(true);
    }

}
