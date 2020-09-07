package com.king.app.coolg.phone.star.random;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.model.setting.SettingProperty;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.ColorUtil;
import com.king.app.coolg.utils.ListUtil;
import com.king.app.coolg.utils.ScreenUtils;
import com.king.app.gdb.data.entity.Star;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
 * @date: 2020/9/7 10:25
 */
public class StarRandomViewModel extends BaseViewModel {

    private List<Star> mCandidates = new ArrayList<>();

    private List<Star> mSelectedList = new ArrayList<>();

    private List<Star> mRandomList = null;

    private Disposable randomDisposable;

    public MutableLiveData<StarProxy> starObserver = new MutableLiveData<>();

    public MutableLiveData<List<Star>> candidatesObserver = new MutableLiveData<>();
    public MutableLiveData<List<Star>> selectedObserver = new MutableLiveData<>();

    public ObservableInt btnControlRes = new ObservableInt(R.drawable.ic_play_circle_filled_black_36dp);
    public ObservableField<String> starName = new ObservableField<>();

    private Random random = new Random();

    private RandomRule randomRule = new RandomRule();

    private StarRepository starRepository = new StarRepository();

    private int maxHeight;
    private int maxWidth;

    private StarProxy mCurrentStar;

    public StarRandomViewModel(@NonNull Application application) {
        super(application);
        maxWidth = ScreenUtils.getScreenWidth() - ScreenUtils.dp2px(16) * 2;
        maxHeight = ScreenUtils.getScreenHeight() - application.getResources().getDimensionPixelSize(R.dimen.star_random_img_top)
            - application.getResources().getDimensionPixelSize(R.dimen.star_random_img_bottom);
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public RandomRule getRandomRule() {
        return randomRule;
    }

    public void saveRandomRule(RandomRule randomRule) {
        this.randomRule = randomRule;
        resetRandomList();
    }

    public void onClickStart(View view) {
        if (randomDisposable == null) {
            startRandom();
            btnControlRes.set(R.drawable.ic_pause_circle_filled_black_36dp);
        }
        else {
            stopRandom();
            btnControlRes.set(R.drawable.ic_play_circle_filled_black_36dp);
        }
        ColorUtil.updateIconColor((ImageView) view, getIconColor());
    }

    public void resetRandomList() {
        mRandomList = null;
    }

    private void startRandom() {
        prepareRandomList()
                .flatMap(list -> {
                    mRandomList = list;
                    return randomStar(list);
                })
                .flatMap(star -> toStarProxy(star))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<StarProxy>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        randomDisposable = d;
                    }

                    @Override
                    public void onNext(StarProxy starProxy) {
                        mCurrentStar = starProxy;
                        starName.set(starProxy.getStar().getName());
                        starObserver.setValue(starProxy);
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

    public void stopRandom() {
        if (randomDisposable != null) {
            randomDisposable.dispose();
            randomDisposable = null;
        }
    }

    private Observable<List<Star>> prepareRandomList() {
        return Observable.create(e -> {
            List<Star> list = new ArrayList<>();
            if (mRandomList == null) {
                // 优先运用candidates
                list = mCandidates;
                // 没有才根据rule
                if (ListUtil.isEmpty(list)) {
                    String[] conditions = null;
                    try {
                        conditions = randomRule.getSqlRating().split(",");
                    } catch (Exception ee){}
                    list = starRepository.queryStar(randomRule.getStarType(), conditions);
                }
            }
            else {
                list = mRandomList;
            }
            if (list.size() == 0) {
                e.onError(new Exception("No random data"));
            }
            else {
                e.onNext(list);
                e.onComplete();
            }
        });
    }

    private Observable<Star> randomStar(List<Star> randomList) {
        return Observable.create(e -> {
            try {
                while (randomDisposable != null) {
                    int index = Math.abs(random.nextInt()) % randomList.size();
                    e.onNext(randomList.get(index));
                    Thread.sleep(150);
                }
            } catch (InterruptedException ee) {
                e.onComplete();
            }
        });
    }

    private ObservableSource<StarProxy> toStarProxy(Star star) {
        return observer -> {
            StarProxy proxy = new StarProxy();
            proxy.setStar(star);
            proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
            calcImageSize(proxy);
            observer.onNext(proxy);
        };
    }

    private void calcImageSize(StarProxy bean) {
        if (bean.getImagePath() != null) {
            //缩放图片的实际宽高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bean.getImagePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;
            // 超出才缩放
            if (height > maxHeight || width > maxWidth) {
                float r = (float) width / (float) height;
                float rm = (float) maxWidth / (float) maxHeight;
                // 按宽缩放
                if (r > rm) {
                    float ratio = (float) maxWidth / (float) width;
                    bean.setWidth(maxWidth);
                    height = (int) (height * ratio);
                    bean.setHeight(height);
                }
                // 按高缩放
                else {
                    float ratio = (float) maxHeight / (float) height;
                    bean.setHeight(maxHeight);
                    width = (int) (width * ratio);
                    bean.setWidth(width);
                }
            }
            else {
                bean.setWidth(width);
                bean.setHeight(height);
            }
        }
    }

    public void setCandidates(ArrayList<CharSequence> list) {
        if (!ListUtil.isEmpty(list)) {
            for (CharSequence idStr:list) {
                long starId = Long.parseLong(idStr.toString());
                Star star = getDaoSession().getStarDao().load(starId);
                // 过滤重复项
                if (!isRepeatStar(star)) {
                    mCandidates.add(star);
                }
            }
            candidatesObserver.setValue(mCandidates);
        }
    }

    private boolean isRepeatStar(Star star) {
        for (Star s: mCandidates) {
            if (s.getId() == star.getId()) {
                return true;
            }
        }
        return false;
    }

    public void deleteCandidate(Star star) {
        mCandidates.remove(star);
        candidatesObserver.setValue(mCandidates);
    }

    public void clearCandidates() {
        mCandidates.clear();
        candidatesObserver.setValue(mCandidates);
    }

    public int getIconColor() {
        return getApplication().getResources().getColor(R.color.red_f1303d);
    }

    public void markCurrentStar() {
        if (mCurrentStar != null) {
            mSelectedList.add(mCurrentStar.getStar());
            selectedObserver.setValue(mSelectedList);
            if (randomRule.isExcludeFromMarked()) {
                mRandomList.remove(mCurrentStar.getStar());
            }
        }
    }

    public void deleteSelected(Star star) {
        mSelectedList.remove(star);
        selectedObserver.setValue(mSelectedList);
        // 还要添加回随机列表
        if (randomRule.isExcludeFromMarked()) {
            // 过滤重复项
            if (!isRepeatStar(star)) {
                mRandomList.add(star);
            }
        }
    }

    public void clearAll() {
        mCandidates.clear();
        mSelectedList.clear();
        randomRule = new RandomRule();
        mCurrentStar = null;
        resetRandomList();
        candidatesObserver.setValue(mCandidates);
        selectedObserver.setValue(mSelectedList);
    }

    @Override
    public void onDestroy() {
        RandomData randomData = new RandomData();
        randomData.setName("auto");
        randomData.setRandomRule(randomRule);
        randomData.setCandidateList(new ArrayList<>());
        randomData.setMarkedList(new ArrayList<>());
        for (Star star:mCandidates) {
            randomData.getCandidateList().add(star.getId());
        }
        for (Star star:mSelectedList) {
            randomData.getMarkedList().add(star.getId());
        }
        SettingProperty.setStarRandomData(randomData);
        super.onDestroy();
    }

    /**
     * 初始化上一次的数据
     */
    public void loadDefaultData() {
        RandomData data = SettingProperty.getStarRandomData();
        randomRule = data.getRandomRule();
        if (randomRule == null) {
            randomRule = new RandomRule();
        }
        for (long id:data.getMarkedList()) {
            try {
                Star star = getDaoSession().getStarDao().load(id);
                if (star != null) {
                    mSelectedList.add(star);
                }
            } catch (Exception e) {}
        }
        selectedObserver.setValue(mSelectedList);

        for (long id:data.getCandidateList()) {
            try {
                Star star = getDaoSession().getStarDao().load(id);
                if (star != null) {
                    mCandidates.add(star);
                }
            } catch (Exception e) {}
        }
        candidatesObserver.setValue(mCandidates);

        prepareRandomList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<Star>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(List<Star> stars) {
                        mRandomList = stars;
                        for (Star mark:mSelectedList) {
                            for (int i = 0; i < mRandomList.size(); i ++) {
                                if (mRandomList.get(i).getId() == mark.getId()) {
                                    mRandomList.remove(i);
                                    break;
                                }
                            }
                        }
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
