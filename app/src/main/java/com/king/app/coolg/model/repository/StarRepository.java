package com.king.app.coolg.model.repository;

import android.graphics.BitmapFactory;

import com.king.app.coolg.conf.AppConstants;
import com.king.app.coolg.conf.RatingType;
import com.king.app.coolg.model.bean.LazyData;
import com.king.app.coolg.model.bean.StarDetailBuilder;
import com.king.app.coolg.model.bean.StarSortBuilder;
import com.king.app.coolg.model.comparator.StarNameComparator;
import com.king.app.coolg.model.comparator.StarRatingComparator;
import com.king.app.coolg.model.comparator.StarRecordsNumberComparator;
import com.king.app.coolg.model.image.ImageProvider;
import com.king.app.coolg.phone.star.list.StarProxy;
import com.king.app.coolg.utils.CostTimeUtil;
import com.king.app.gdb.data.entity.FavorRecordOrder;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarDao;
import com.king.app.gdb.data.entity.StarRating;
import com.king.app.gdb.data.entity.StarRatingDao;
import com.king.app.gdb.data.entity.TagStar;
import com.king.app.gdb.data.entity.TagStarDao;
import com.king.app.gdb.data.param.DataConstants;

import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2018/8/9 15:31
 */
public class StarRepository extends BaseRepository {

    public Star getStarBean(long starId) {
        return getDaoSession().getStarDao().load(starId);
    }

    public Observable<Star> getStar(long starId) {
        return Observable.create(e -> e.onNext(getStarBean(starId)));
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

    public Observable<List<Star>> queryStudioStars(long studioId, String starType) {
        return Observable.create(e -> {
            List<Star> stars = new ArrayList<>();
            FavorRecordOrder order = getDaoSession().getFavorRecordOrderDao().load(studioId);
            if (order != null) {
                List<Record> records = order.getRecordList();
                Map<Long, RecordStar> starMap = new HashMap<>();
                for (Record record:records) {
                    List<RecordStar> recordStars = record.getRelationList();
                    for (RecordStar rs:recordStars) {
                        if (starMap.get(rs.getStarId()) == null) {
                            if (DataConstants.STAR_MODE_TOP.equals(starType)) {
                                if (rs.getStar().getBetop() > 0 && rs.getStar().getBebottom() == 0) {
                                    stars.add(rs.getStar());
                                }
                            }
                            else if (DataConstants.STAR_MODE_BOTTOM.equals(starType)) {
                                if (rs.getStar().getBebottom() > 0 && rs.getStar().getBetop() == 0) {
                                    stars.add(rs.getStar());
                                }
                            }
                            else if (DataConstants.STAR_MODE_HALF.equals(starType)) {
                                if (rs.getStar().getBebottom() > 0 && rs.getStar().getBetop() > 0) {
                                    stars.add(rs.getStar());
                                }
                            }
                            else {
                                stars.add(rs.getStar());
                            }
                            starMap.put(rs.getStarId(), rs);
                        }
                    }
                }
            }
            e.onNext(stars);
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

    public Observable<List<StarProxy>> sortStars(List<StarProxy> list, int sortType) {
        return Observable.create(e -> {
            if (sortType == AppConstants.STAR_SORT_RANDOM) {// order by records number
                Collections.shuffle(list);
            }
            else if (sortType == AppConstants.STAR_SORT_RECORDS) {// order by records number
                Collections.sort(list, new StarRecordsNumberComparator());
            }
            else if (sortType == AppConstants.STAR_SORT_RATING) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.COMPLEX));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_FACE) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.FACE));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_BODY) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.BODY));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_DK) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.DK));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_SEXUALITY) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.SEXUALITY));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_PASSION) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.PASSION));
            }
            else if (sortType == AppConstants.STAR_SORT_RATING_VIDEO) {// order by rating
                Collections.sort(list, new StarRatingComparator(RatingType.VIDEO));
            }
            else {
                // order by name
                Collections.sort(list, new StarNameComparator());
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    private StarProxy toStarProxy(Star star) {
        StarProxy proxy = new StarProxy();
        if (star.getName() == null) {
            return null;
        }
        proxy.setStar(star);
        proxy.setImagePath(ImageProvider.getStarRandomPath(star.getName(), null));
        return proxy;
    }

    public Observable<List<StarProxy>> queryTagStars(Long tagId) {
        return Observable.create(e -> {
            List<StarProxy> list = new ArrayList<>();
            // 全部
            if (tagId == null) {
                List<Star> stars = getDaoSession().getStarDao().queryBuilder()
                        .build().list();
                for (Star star:stars) {
                    StarProxy proxy = toStarProxy(star);
                    if (proxy != null) {
                        list.add(proxy);
                    }
                }
            }
            else {
                QueryBuilder<Star> builder = getDaoSession().getStarDao().queryBuilder();
                Join<Star, TagStar> join = builder.join(TagStar.class, TagStarDao.Properties.StarId);
                join.where(TagStarDao.Properties.TagId.eq(tagId));
                List<Star> stars = builder.list();
                for (Star star:stars) {
                    StarProxy proxy = toStarProxy(star);
                    if (proxy != null) {
                        list.add(proxy);
                    }
                }
            }
            e.onNext(list);
            e.onComplete();
        });
    }

    public Observable<List<StarProxy>> queryStarsBy(StarSortBuilder starBuilder) {
        return Observable.create(e -> {
            CostTimeUtil.start();
            List<StarProxy> list = new ArrayList<>();
            QueryBuilder<Star> builder = getDaoSession().getStarDao().queryBuilder();
            if (starBuilder.getTagId() != null) {
                Join<Star, TagStar> join = builder.join(TagStar.class, TagStarDao.Properties.StarId);
                join.where(TagStarDao.Properties.TagId.eq(starBuilder.getTagId()));
            }
            if (starBuilder.isOrderByName()) {
                builder.orderAsc(StarDao.Properties.Name);
            }
            else if (starBuilder.isOrderByRecords()) {
                builder.orderDesc(StarDao.Properties.Records);
            }
            else if (starBuilder.isOrderByRandom()) {
                builder.orderRaw("RANDOM()");
            }
            else if (starBuilder.getOrderByRatingProperty() != null) {
                Join<Star, StarRating> join = builder.join(StarRating.class, StarRatingDao.Properties.StarId);
                builder.orderRaw(join.getTablePrefix() + "." + starBuilder.getOrderByRatingProperty().name + " DESC");
            }
            List<Star> stars = builder.list();
            for (Star star:stars) {
                if (star.getName() == null) {
                    continue;
                }
                StarProxy proxy = new StarProxy();
                proxy.setStar(star);
                list.add(proxy);
            }
            CostTimeUtil.end("queryStarsBy");
            e.onNext(list);
            e.onComplete();
        });
    }

    /**
     * 每加载指定个数时通知
     * @param list
     * @param num
     * @return
     */
    public Observable<LazyData<StarProxy>> lazyLoad(List<StarProxy> list, int num, StarDetailBuilder builder) {
        return Observable.create(e -> {
            int count = 0;
            int start = 0;
            CostTimeUtil.start();
            for (StarProxy proxy:list) {
                // 设置图片路径（耗时操作）
                if (builder.isLoadImageSize()) {
                    proxy.setImagePath(ImageProvider.getStarRandomPath(proxy.getStar().getName(), null));
                }
                // 瀑布流还要计算图片合适尺寸（耗时操作）
                if (builder.isLoadImageSize()) {
                    calcImageSize(proxy, builder);
                }
                // 加载rating数据（第一次执行lazyLoads属于耗时操作，之后就属于GreenDao的缓存中了，变为非耗时操作）
                if (builder.isLoadRating()) {
                    proxy.getStar().getRatings();
                }

                count ++;
                // 满足num个进行通知
                if (count == num) {
                    e.onNext(new LazyData<>(start, count, list));
                    start = count;
                    count = 0;
                    CostTimeUtil.end("onNext");
                    CostTimeUtil.start();
                }
            }
            // 处理最后一批不足num的情况
            if (count != num) {
                e.onNext(new LazyData<>(start, count, list));
            }
            e.onComplete();
        });
    }

    private void calcImageSize(StarProxy bean, StarDetailBuilder builder) {
        // 无图按16:9
        if (bean.getImagePath() == null) {
            bean.setWidth(builder.getSizeBaseWidth());
            bean.setHeight(builder.getSizeBaseWidth() * 9 / 16);
        }
        else {
            //缩放图片的实际宽高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bean.getImagePath(), options);
            int height = options.outHeight;
            int width = options.outWidth;
            float ratio = (float) builder.getSizeBaseWidth() / (float) width;
            bean.setWidth(builder.getSizeBaseWidth());
            height = (int) (height * ratio);
            bean.setHeight(height);
        }
    }

}
