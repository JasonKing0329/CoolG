package com.king.app.coolg.model.repository;

import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.StarRatingDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:31
 */
public class StarRepository extends BaseRepository {

    public Observable<Star> getStar(long starId) {
        return Observable.create(e -> e.onNext(getDaoSession().getStarDao().load(starId)));
    }

    public Observable<List<Star>> queryStar(String mode) {
        return Observable.create(e -> {
            StarDao dao = getDaoSession().getStarDao();
            QueryBuilder<Star> builder = dao.queryBuilder();

            // don't show star without records
            builder.where(StarDao.Properties.Records.gt(0));

            if (DataConstants.STAR_MODE_TOP.equals(mode)) {
                builder.where(StarDao.Properties.Betop.gt(0)
                        , StarDao.Properties.Bebottom.eq(0));
            }
            else if (DataConstants.STAR_MODE_BOTTOM.equals(mode)) {
                builder.where(StarDao.Properties.Bebottom.gt(0)
                        , StarDao.Properties.Betop.eq(0));
            }
            else if (DataConstants.STAR_MODE_HALF.equals(mode)) {
                builder.where(StarDao.Properties.Bebottom.gt(0)
                        , StarDao.Properties.Betop.gt(0));
            }
            e.onNext(builder.build().list());
            e.onComplete();
        });
    }

    public List<Star> getRandomRatingAbove(float complex, int number) {
        StarDao dao = getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();
        Join<Star, StarRating> join = builder.join(StarRating.class, StarRatingDao.Properties.StarId);
        join.where(StarRatingDao.Properties.Complex.ge(complex));
        return builder
                .orderRaw("RANDOM()")
                .limit(number)
                .build().list();
    }

    public List<Star> getRandomStars(int number) {
        StarDao dao = getDaoSession().getStarDao();
        return dao.queryBuilder()
                .orderRaw("RANDOM()")
                .limit(number)
                .build().list();
    }

    public long queryStarCount(String mode) {
        StarDao dao = getDaoSession().getStarDao();
        QueryBuilder<Star> builder = dao.queryBuilder();

        // don't show star without records
        builder.where(StarDao.Properties.Records.gt(0));

        if (DataConstants.STAR_MODE_TOP.equals(mode)) {
            builder.where(StarDao.Properties.Betop.gt(0)
                    , StarDao.Properties.Bebottom.eq(0));
        }
        else if (DataConstants.STAR_MODE_BOTTOM.equals(mode)) {
            builder.where(StarDao.Properties.Bebottom.gt(0)
                    , StarDao.Properties.Betop.eq(0));
        }
        else if (DataConstants.STAR_MODE_HALF.equals(mode)) {
            builder.where(StarDao.Properties.Bebottom.gt(0)
                    , StarDao.Properties.Betop.gt(0));
        }
        return builder.buildCount().count();
    }

}
