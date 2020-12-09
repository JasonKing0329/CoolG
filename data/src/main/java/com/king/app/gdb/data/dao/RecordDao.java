package com.king.app.gdb.data.dao;

import android.arch.persistence.room.Query;

import com.king.app.gdb.data.entity.CountRecord;
import com.king.app.gdb.data.entity.Record;
import com.king.app.gdb.data.entity.RecordStar;
import com.king.app.gdb.data.entity.RecordType1v1;
import com.king.app.gdb.data.entity.RecordType3w;

import java.util.List;

/**
 * @description:
 * @author：Jing
 * @date: 2020/12/9 22:19
 */
public interface RecordDao {

    @Query("select * from record where id=:id")
    RecordType1v1 getRecord(long id);

    @Query("select * from record_type1 where id=:id")
    RecordType1v1 getRecordType1v1(long id);

    @Query("select * from record_type3 where id=:id")
    RecordType3w getRecordType3w(long id);

    @Query("select * from count_record where recordId=:id")
    CountRecord getCountRecord(long id);

    @Query("select * from record_star where recordId=:id")
    RecordStar getRecordStarRelations(long id);

    @Query("select * from record r join record_star rs on r.id=rs.recordId where rs.starId=:starId")
    List<Record> getStarRecords(long starId);

    @Query("select * from record r join favor_record fr on r.id=fr.recordId where fr.orderId=:orderId")
    List<Record> getFavRecords(long orderId);
}
