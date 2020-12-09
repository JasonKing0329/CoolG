package com.king.app.gdb.data.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.List;

/**
 * 描述:
 * <p/>作者：景阳
 * <p/>创建时间: 2018/2/9 11:38
 */
@Entity(tableName = "stars")
public class Star {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String name;
    private int records;
    private int betop;
    private int bebottom;
    private float average;
    private int max;
    private int min;
    private float caverage;
    private int cmax;
    private int cmin;

    private int favor;

    @Ignore
    private StarRating ratings;// StarDao.getStarRating(starId)

    @Ignore
    private CountStar countStar;// StarDao.getCountStar(starId)

    @Ignore
    private List<Record> recordList;// RecordDao.getStarRecords(id)

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRecords() {
        return this.records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public int getBetop() {
        return this.betop;
    }

    public void setBetop(int betop) {
        this.betop = betop;
    }

    public int getBebottom() {
        return this.bebottom;
    }

    public void setBebottom(int bebottom) {
        this.bebottom = bebottom;
    }

    public float getAverage() {
        return this.average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public float getCaverage() {
        return this.caverage;
    }

    public void setCaverage(float caverage) {
        this.caverage = caverage;
    }

    public int getCmax() {
        return this.cmax;
    }

    public void setCmax(int cmax) {
        this.cmax = cmax;
    }

    public int getCmin() {
        return this.cmin;
    }

    public void setCmin(int cmin) {
        this.cmin = cmin;
    }

    public int getFavor() {
        return this.favor;
    }

    public void setFavor(int favor) {
        this.favor = favor;
    }

    public StarRating getRatings() {
        return ratings;
    }

    public void setRatings(StarRating ratings) {
        this.ratings = ratings;
    }

    public CountStar getCountStar() {
        return countStar;
    }

    public void setCountStar(CountStar countStar) {
        this.countStar = countStar;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }
}
