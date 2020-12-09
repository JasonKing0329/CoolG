package com.king.app.gdb.data.dao;

import android.arch.persistence.room.Query;

import com.king.app.gdb.data.entity.Tag;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface TagDao {

    @Query("select * from tag where id=:id")
    Tag getTag(long id);

}
