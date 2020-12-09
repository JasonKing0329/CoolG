package com.king.app.gdb.data.dao;

import android.arch.persistence.room.Query;

import com.king.app.gdb.data.entity.CountStar;
import com.king.app.gdb.data.entity.Star;
import com.king.app.gdb.data.entity.StarRating;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface StarDao {

    @Query("select * from stars where id=:id")
    StarRating getStar(long id);

    @Query("select * from star_rating where starId=:id")
    StarRating getStarRating(long id);

    @Query("select * from count_star where starId=:id")
    CountStar getCountStar(long id);

    @Query("select * from stars s join record_star rs on s.id=rs.starId where rs.recordId=:recordId")
    List<Star> getRecordStars(long recordId);

    @Query("select * from stars s join favor_star fs on s.id=fs.starId where fs.orderId=:orderId")
    List<Star> getFavStars(long orderId);
}
