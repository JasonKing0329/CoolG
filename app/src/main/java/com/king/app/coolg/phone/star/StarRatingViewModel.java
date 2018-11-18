package com.king.app.coolg.phone.star;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;

import com.king.app.coolg.R;
import com.king.app.coolg.base.BaseViewModel;
import com.king.app.coolg.model.ImageProvider;
import com.king.app.coolg.model.palette.PaletteUtil;
import com.king.app.coolg.model.repository.StarRepository;
import com.king.app.coolg.utils.FormatUtil;
import com.king.app.coolg.utils.StarRatingUtil;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.StarRatingDao;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 16:27
 */
public class StarRatingViewModel extends BaseViewModel {

    private Star mStar;

    private StarRating mRating;

    private StarRepository repository;

    public MutableLiveData<Star> starObserver = new MutableLiveData<>();
    public MutableLiveData<StarRating> ratingObserver = new MutableLiveData<>();

    public StarRatingViewModel(@NonNull Application application) {
        super(application);
        repository = new StarRepository();
    }

    public void loadStarRating(long starId) {
        repository.getStar(starId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Star>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Star star) {
                        mStar = star;
                        starObserver.setValue(star);
                        if (star.getRatings().size() > 0) {
                            mRating = star.getRatings().get(0);
                            ratingObserver.setValue(mRating);
                        }
                        else {
                            mRating = new StarRating();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public String getComplex() {
        float complex = calculateComplex(mRating);
        return StarRatingUtil.getRatingValue(complex) + "(" + FormatUtil.formatScore(complex, 2) + ")";
    }

    public String getStarImage() {
        return ImageProvider.getStarRandomPath(mStar.getName(), null);
    }

    public StarRating getRating() {
        return mRating;
    }

    public void saveRatingChange() {
        getDaoSession().getStarRatingDao().update(mRating);
        getDaoSession().getStarRatingDao().detach(mRating);
    }

    public void saveRating() {
        mRating.setStarId(mStar.getId());
        mRating.setComplex(calculateComplex(mRating));
        StarRatingDao dao = getDaoSession().getStarRatingDao();
        dao.insertOrReplace(mRating);
        dao.detachAll();
        mStar.resetRatings();
        messageObserver.setValue("Save successfully");
    }

    private float calculateComplex(StarRating mRating) {
        return mRating.getBody() * 0.2f
                + mRating.getFace() * 0.18f
                + mRating.getDk() * 0.1f
                + mRating.getSexuality() * 0.25f
                + mRating.getPassion() * 0.17f
                + mRating.getVideo() * 0.1f;
    }

    public int generateStarColor(Resources resources, Palette palette) {
        Palette.Swatch swatch = PaletteUtil.getDefaultSwatch(palette);
        if (swatch == null) {
            return resources.getColor(R.color.colorAccent);
        }
        else {
            return swatch.getRgb();
        }
    }
}
